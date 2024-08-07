package io.management.ua.products.service;

import com.fasterxml.jackson.core.type.TypeReference;
import io.management.resources.models.Image;
import io.management.resources.service.ImageHostingService;
import io.management.ua.amqp.messages.MessageModel;
import io.management.ua.annotations.DefaultNumberValue;
import io.management.ua.annotations.DefaultStringValue;
import io.management.ua.category.repository.CategoryRepository;
import io.management.ua.exceptions.ActionRestrictedException;
import io.management.ua.exceptions.DefaultException;
import io.management.ua.exceptions.NotFoundException;
import io.management.ua.producers.MessageProducer;
import io.management.ua.products.attributes.Currency;
import io.management.ua.products.dto.*;
import io.management.ua.products.entity.Product;
import io.management.ua.products.entity.WaitingListProduct;
import io.management.ua.products.mapper.ProductMapper;
import io.management.ua.products.repository.ProductRepository;
import io.management.ua.products.repository.WaitingListProductRepository;
import io.management.ua.utility.*;
import io.management.ua.utility.api.enums.Folder;
import io.management.ua.utility.models.NetworkResponse;
import io.management.ua.utility.models.UserSecurityRole;
import io.management.ua.utility.network.NetworkService;
import io.management.users.models.UserDetailsModel;
import io.management.users.service.UserDetailsImplementationService;
import io.netty.util.internal.StringUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
@Validated
public class ProductService {
    @PersistenceContext(unitName = "database")
    private final EntityManager entityManager;
    private final JdbcTemplate jdbcTemplate;
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final ImageHostingService imageHostingService;
    private final WaitingListProductRepository waitingListProductRepository;
    private final UserDetailsImplementationService userDetailsImplementationService;
    private final MessageProducer messageProducer;
    private final ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(8);
    private final Map<String, ApplicationCourseCurrencyU> currencyCourses = new HashMap<>();

    private final NetworkService networkService;

    @Scheduled(cron = "0 0 0 * * *")
    @PostConstruct
    public void initCurrencyCourses() {
        try {
            NetworkResponse networkResponse = networkService.performRequest("https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5");
            List<ApplicationCourseCurrencyU> applicationCourseCurrencies = UtilManager.objectMapper()
                    .readValue(networkResponse.getBody().toString(), new TypeReference<>() {
                    });

            for (ApplicationCourseCurrencyU applicationCourseCurrencyU : applicationCourseCurrencies) {
                currencyCourses.put(applicationCourseCurrencyU.getCurrency(), applicationCourseCurrencyU);
            }
        } catch (URISyntaxException | IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public List<ProductLinkDTO> getProductLinksByNamePartial(String searchBy, @DefaultNumberValue Integer page) {
        List<ProductLinkDTO> productLinks = new ArrayList<>();

        Page<Product> productMatchesByNamePartial = productRepository.getProductsByNamePartial(searchBy.toLowerCase(), PageRequest.of(page, 10));

        if (productMatchesByNamePartial.hasContent()) {
            for (Product product : productMatchesByNamePartial.getContent()) {
                ProductLinkDTO productLink = new ProductLinkDTO();
                productLink.setProductName(product.getName());
                productLink.setProductId(product.getProductId());
                productLink.setCategoryName(categoryRepository.getCategoryNameByCategoryId(product.getCategoryId()));

                productLinks.add(productLink);
            }
        }

        return productLinks;
    }

    public List<Product> getProducts(@Nullable @Valid ProductFilter productFilter,
                                     @DefaultNumberValue Integer page,
                                     @DefaultNumberValue(number = 100) Integer size,
                                     @DefaultStringValue(string = "cost") String sortBy,
                                     @DefaultStringValue(string = "DESC") String direction) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Product> query = criteriaBuilder.createQuery(Product.class);
        Root<Product> root = query.from(Product.class);

        List<Predicate> predicates = new ArrayList<>();

        if (productFilter != null) {
            if (!StringUtil.isNullOrEmpty(productFilter.getName())) {
                predicates.add(criteriaBuilder.like(root.get(Product.Fields.name), "%" + productFilter.getName() + "%"));
            }

            if (!StringUtil.isNullOrEmpty(productFilter.getBrand())) {
                predicates.add(criteriaBuilder.equal(root.get(Product.Fields.brand), productFilter.getBrand()));
            }

            if (productFilter.getProductIds() != null && !productFilter.getProductIds().isEmpty()) {
                predicates.add(root.get(Product.Fields.productId).in(productFilter.getProductIds()));
            }

            if (productFilter.getPriceFrom() != null && productFilter.getPriceFrom() > 0) {
                predicates.add(criteriaBuilder.ge(root.get(Product.Fields.cost), productFilter.getPriceFrom()));
            }

            if (productFilter.getPriceTo() != null && productFilter.getPriceTo() < Integer.MAX_VALUE) {
                predicates.add(criteriaBuilder.le(root.get(Product.Fields.cost), productFilter.getPriceTo()));
            }

            if (productFilter.getIsPresent() != null) {
                if (productFilter.getIsPresent()) {
                    predicates.add(criteriaBuilder.isFalse(root.get(Product.Fields.blocked)));
                    predicates.add(criteriaBuilder.greaterThan(root.get(Product.Fields.itemsLeft), 0));
                } else {
                    predicates.add(criteriaBuilder.le(root.get(Product.Fields.itemsLeft), 0));
                }
            }

            if (productFilter.getIsBlocked() != null) {
                if (productFilter.getIsBlocked()) {
                    predicates.add(criteriaBuilder.isTrue(root.get(Product.Fields.blocked)));
                } else {
                    predicates.add(criteriaBuilder.isFalse(root.get(Product.Fields.blocked)));
                }
            }

            if (productFilter.getCategoryId() != null) {
                List<UUID> childCategories = new ArrayList<>();
                jdbcTemplate.query(ResourceLoaderUtil.getResourceContent(Scripts.getSubCategoriesEnabledTree), (rs) -> {
                            childCategories.add(rs.getObject("category_id", UUID.class));
                        },
                        productFilter.getCategoryId());
                predicates.add(root.get(Product.Fields.categoryId).in( childCategories));
            }
        }

        query.where(predicates.toArray(Predicate[]::new));

        if (direction != null) {
            switch (Sort.Direction.valueOf(direction)) {
                case DESC -> query.orderBy(criteriaBuilder.desc(root.get(sortBy)));
                case ASC -> query.orderBy(criteriaBuilder.asc(root.get(sortBy)));
            }
        } else {
            query.orderBy(criteriaBuilder.desc(root.get(Product.Fields.id)));
        }

        return entityManager.createQuery(query).setFirstResult((page - 1) * size).setMaxResults(size).getResultList();
    }

    public Product saveProduct(@Valid CreateProductDTO createProductDTO) {
        Product product = productMapper.dtoToEntity(createProductDTO);
        product.setProductId(UUID.randomUUID());

        return productRepository.save(product);
    }

    public Product updateProduct(@Valid UpdateProductDTO updateProductDTO) {
        Product product = productRepository.findByProductId(updateProductDTO.getProductId())
                .orElseThrow(() -> new NotFoundException("Product with ID {} was not found", updateProductDTO.getProductId()));

        if (!StringUtil.isNullOrEmpty(updateProductDTO.getName())) {
            product.setName(updateProductDTO.getName());
        }

        if (!StringUtil.isNullOrEmpty(updateProductDTO.getBrand())) {
            product.setBrand(updateProductDTO.getBrand());
        }

        if (updateProductDTO.getParameters() != null && !updateProductDTO.getParameters().isEmpty()) {
            Map<String, String> validParameters = new HashMap<>();

            for (Map.Entry<String, String> entry : updateProductDTO.getParameters().entrySet()) {
                if (!StringUtil.isNullOrEmpty(entry.getKey()) && !StringUtil.isNullOrEmpty(entry.getValue())) {
                    validParameters.put(entry.getKey(), entry.getValue());
                }
            }

            product.setParameters(validParameters);
        }

        if (!StringUtil.isNullOrEmpty(updateProductDTO.getDescription())) {
            product.setDescription(updateProductDTO.getDescription());
        }

        if (updateProductDTO.getCost() != null && updateProductDTO.getCost().compareTo(BigDecimal.ZERO) > 0) {
            product.setCost(updateProductDTO.getCost());
        }

        if (updateProductDTO.getCurrency() != null) {
            product.setCurrency(updateProductDTO.getCurrency());
        }

        if (updateProductDTO.getItemsLeft() != null && updateProductDTO.getItemsLeft() >= 0) {
            if (product.getItemsLeft() == 0 && updateProductDTO.getItemsLeft() > 0) {
                notifyWaitingCustomers(product);
            }

            if (product.getItemsLeft() >= 30 && updateProductDTO.getItemsLeft() < 30) {
                notifyManager(product);
            }
            product.setItemsLeft(updateProductDTO.getItemsLeft());
        }

        if (updateProductDTO.getBlocked() != null) {
            product.setBlocked(updateProductDTO.getBlocked());
        }

        if (updateProductDTO.getCategoryId() != null) {
            product.setCategoryId(updateProductDTO.getCategoryId());
        }

        if (updateProductDTO.getMarginRate() != null) {
            product.setMarginRate(updateProductDTO.getMarginRate());
        }

        return productRepository.save(product);
    }

    public Pair<Product, Integer> getProductOrderingPair(OrderedProductDTO orderedProductDTO) {
        Product product = productRepository.findByProductId(orderedProductDTO.getProductId())
                .orElseThrow(() -> new NotFoundException("Product with ID {} was not found for ordering",
                        orderedProductDTO.getProductId()));

        return Pair.of(product, orderedProductDTO.getAmount());
    }

    public Product setProductIntroductionPicture(UUID productId, MultipartFile picture) {
        Product product = productRepository.findByProductId(productId)
                .orElseThrow(() -> new NotFoundException("Product with product ID {} was not found", productId));

        if (product.getIntroductionPictureUrl() != null) {
            imageHostingService.deleteImage(product.getIntroductionPictureUrl());
            product.setIntroductionPictureUrl(null);
        }

        if (picture != null) {
            Image image = imageHostingService.uploadImage(picture, Folder.PRODUCTS + product.getProductId());
            product.setIntroductionPictureUrl(image.getSecureUrl());
        }

        return productRepository.save(product);
    }

    public Product setProductPictures(UUID productId, List<MultipartFile> picturesToAdd) {
        if ((picturesToAdd == null || picturesToAdd.isEmpty())) {
            throw new DefaultException("Product pictures setting invocation is invalid");
        }

        Product product = productRepository.findByProductId(productId)
                .orElseThrow(() -> new NotFoundException("Product with product ID {} was not found", productId));

        List<String> uwu = new ArrayList<>();

        if (product.getPictureUrls() == null) {
            product.setPictureUrls(new String[]{});
        } else {
            uwu = new ArrayList<>(List.of(product.getPictureUrls()));
        }

        for (MultipartFile picture : picturesToAdd) {
            Image image = imageHostingService.uploadImage(picture, Folder.PRODUCTS + product.getProductId());

            List<String> urls = new ArrayList<>(Arrays.asList(product.getPictureUrls()));
            urls.add(image.getSecureUrl());
            product.setPictureUrls(urls.toArray(String[]::new));
        }

        for (String url : uwu) {
            imageHostingService.deleteImage(url);
        }

        return productRepository.save(product);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void addProductSaleStatisticEntry(ProductSaleStatisticEntry productSaleStatisticEntry) {
        jdbcTemplate.update(ResourceLoaderUtil.getResourceContent(Scripts.addProductSaleStatisticEntry),
                productSaleStatisticEntry.getProductId(),
                productSaleStatisticEntry.getItemsSold(),
                productSaleStatisticEntry.getTotalCost(),
                productSaleStatisticEntry.getCategoryId(),
                productSaleStatisticEntry.getProductId());
    }

    public void exportProducts(ProductFilter productFilter, String filename, HttpServletResponse httpServletResponse) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Products");
            sheet.setDefaultColumnWidth(50);

            httpServletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            httpServletResponse.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s.xlsx", ExportUtil.validateFilename(filename)));
            OutputStream outputStream = httpServletResponse.getOutputStream();

            int page = 1;
            int limitPerPage = 500;
            List<Product> products;

            do {
                products = getProducts(productFilter, page, limitPerPage, null, null);

                if (!products.isEmpty()) {
                    Row headerRow = sheet.createRow(0);
                    Field[] fields = products.get(0).getClass().getDeclaredFields();
                    for (int i = 0; i < fields.length; i++) {
                        headerRow.createCell(i).setCellValue(ExportUtil.convertFieldNameToTitle(fields[i].getName()));
                    }

                    int rowNum = 1;

                    for (Product product : products) {
                        Row row = sheet.createRow(rowNum++);
                        int colNum = 0;
                        for (Field field : fields) {
                            field.setAccessible(true);
                            try {
                                Object value = field.get(product);
                                if (value != null) {
                                    ExportUtil.setCellValue(row.createCell(colNum++), value);
                                } else {
                                    row.createCell(colNum++).setCellValue("NONE");
                                }
                            } catch (IllegalAccessException e) {
                                log.error(e.getMessage(), e);
                            }
                        }
                    }
                    workbook.write(outputStream);
                    page++;
                } else {
                    if (page == 1) {
                        httpServletResponse.setContentType(MediaType.APPLICATION_JSON_VALUE);
                        httpServletResponse.setHeader(HttpHeaders.CONTENT_DISPOSITION, "inline");
                    }
                }
            } while (!products.isEmpty());

            workbook.close();
            outputStream.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new DefaultException("Exception occurred while exporting");
        }
    }

    public Long getProductAmount(UUID productId) {
        if (productRepository.existsByProductId(productId)) {
            throw new NotFoundException("Product with product ID {} was not found", productId);
        }

        Long amount = productRepository.getProductAmount(productId);

        return amount == null ? 0 : amount;
    }

    public List<WaitingListProductDTO> getWaitingList(@NotNull(message = "Customer ID can not be null")
                                                      @Min(value = 1, message = "Invalid customer ID")
                                                      Long customerId) {
        List<UUID> productIds = waitingListProductRepository.getProductIdsByCustomerId(customerId);

        if (!productIds.isEmpty()) {
            return productRepository.getWaitingListForCustomer(productIds);
        } else {
            return List.of();
        }
    }

    public WaitingListProduct addProductToWaitingList(Long customerId, UUID productId) {
        if (waitingListProductRepository.existsByCustomerIdAndProductId(customerId, productId)) {
            throw new DefaultException("Product is already in waiting list...");
        }

        WaitingListProduct waitingListProduct = new WaitingListProduct();
        waitingListProduct.setCustomerId(customerId);
        waitingListProduct.setProductId(productId);

        return waitingListProductRepository.save(waitingListProduct);
    }

    public boolean removeProductFromWaitingList(Long customerId, UUID productId) {
        int numberOfAffectedEntries = waitingListProductRepository.removeWaitingListEntry(customerId, productId);

        return numberOfAffectedEntries > 0;
    }

    @Transactional
    public void orderProduct(Long orderId, Long productId, Integer productAmount) {
        jdbcTemplate.update(ResourceLoaderUtil.getResourceContent(Scripts.addOrderedProductEntry), orderId, productId, productAmount);
        Product product = productRepository.findById(productId).orElseThrow(() -> new NotFoundException("Product with ID {} was not found", productId));

        if (productAmount > product.getItemsLeft()) {
            throw new ActionRestrictedException("Product amount for order is bigger than actual amount at store");
        } else {
            product.setItemsLeft(product.getItemsLeft() - productAmount);
            productRepository.save(product);
        }

        if (product.getItemsLeft() < 30) {
            notifyManager(product);
        }
    }

    public Product getProduct(UUID productId) {
        return productRepository.findByProductId(productId).orElseThrow(() -> new NotFoundException("Product with product ID {} was not found", productId));
    }

    public List<ProductSaleStatisticView> getSalesStatistic(UUID productId, Date from, Date to) {
        String script = ResourceLoaderUtil.getResourceContent(Scripts.getProductSalesStatistic);

        if (from == null) {
            from = new Date(0);
        }
        if (to == null) {
            to = new Date();
        }

        Map<Date, ProductSaleStatisticView> list = new HashMap<>();

        jdbcTemplate.query(script, resultSet -> {
                    try {
                        Date date = resultSet.getDate("time");
                        if (list.containsKey(date)) {
                            ProductSaleStatisticView view = list.get(date);

                            view.setItemsSold(view.getItemsSold() + resultSet.getInt("items_sold"));
                            view.setTotalCost(view.getTotalCost().add(resultSet.getBigDecimal("total_cost")));
                        } else {
                            ProductSaleStatisticView productSaleStatistic = new ProductSaleStatisticView();

                            productSaleStatistic.setProductId(productId);
                            productSaleStatistic.setTotalCost(resultSet.getBigDecimal("total_cost"));
                            productSaleStatistic.setItemsSold(resultSet.getInt("items_sold"));
                            productSaleStatistic.setDate(date);

                            list.put(date, productSaleStatistic);
                        }
                    } catch (Exception ignored) {

                    }
                }, productId, from, to
        );

        return new ArrayList<>(list.values());
    }

    public Map<Date, BigDecimal> getProfitStatistic(Date from, Date to, @DefaultStringValue(string = "USD") String currency) {
        ApplicationCourseCurrencyU coefficient = currencyCourses.get("USD");
        Double uniqueCoefficientU;
        String script;
        if (currency.equals("USD")) {
            script = ResourceLoaderUtil.getResourceContent(Scripts.getProfitByDayInRangeUS);
            uniqueCoefficientU = Double.valueOf(coefficient.getBuy());
        } else {
            script = ResourceLoaderUtil.getResourceContent(Scripts.getProfitByDayInRangeUA);
            uniqueCoefficientU = Double.valueOf(coefficient.getSale());
        }

        if (from == null) {
            from = new Date(0);
        }
        if (to == null) {
            to = new Date();
        }


        Map<Date, BigDecimal> dayToProfit = new HashMap<>();

        jdbcTemplate.query(script, resultSet -> {
                    try {
                        Date date = resultSet.getDate("time");
                        if (dayToProfit.containsKey(date)) {
                            BigDecimal profit = dayToProfit.get(date);

                            dayToProfit.put(date, profit.add(resultSet.getBigDecimal("ovle")));
                        } else {

                            dayToProfit.put(date, resultSet.getBigDecimal("ovle"));
                        }
                    } catch (Exception ignored) {

                    }
                }, uniqueCoefficientU, from, to
        );

        return dayToProfit;
    }

    public List<AcquireLeads> getUserTopProfit(Date from, Date to, @DefaultNumberValue Integer page, @DefaultStringValue(string = "USD") String currency) {
        ApplicationCourseCurrencyU coefficient = currencyCourses.get("USD");
        Double uniqueCoefficientU;
        String script;
        if (currency.equals("USD")) {
            script = ResourceLoaderUtil.getResourceContent(Scripts.getUserTopProfitListUS);
            uniqueCoefficientU = Double.valueOf(coefficient.getBuy());
        } else {
            script = ResourceLoaderUtil.getResourceContent(Scripts.getUserTopProfitListUA);
            uniqueCoefficientU = Double.valueOf(coefficient.getSale());
        }

        if (from == null) {
            from = new Date(0);
        }
        if (to == null) {
            to = new Date();
        }

        List<AcquireLeads> list = new ArrayList<>();

        jdbcTemplate.query(script, resultSet -> {
            AcquireLeads acquireLeads = new AcquireLeads();
            acquireLeads.setCustomerId(resultSet.getLong("customer_id"));
            acquireLeads.setCurrency(Currency.valueOf(currency));
            acquireLeads.setTotalProfitByCustomer(resultSet.getBigDecimal("sum"));

            list.add(acquireLeads);
        }, uniqueCoefficientU, from, to, 100, 100 * (page - 1));

        for (AcquireLeads acquireLeads : list) {
            UserDetailsModel use = userDetailsImplementationService.getUserById(acquireLeads.getCustomerId());

            if (use != null) {
                if (use.getEmail() != null) {
                    acquireLeads.getContact().add(use.getEmail());
                }
                if (use.getTelegramUsername() != null) {
                    acquireLeads.getContact().add("https://telegram.me/" + use.getTelegramUsername());
                }
            }
        }

        return list;
    }

    public void notifyWaitingCustomers(Product product) {
        Runnable mailingTask = () -> {
            List<Long> list = waitingListProductRepository.getCustomerIdsByProductId(product.getProductId());

            if (list != null && !list.isEmpty()) {
                for (Long i : list) {
                    UserDetailsModel userDetailsModel = userDetailsImplementationService.getUserById(i);

                    if (userDetailsModel != null) {

                        MessageModel messageModel = new MessageModel();

                        messageModel.setSender("CRM");

                        if (userDetailsModel.getEmail() != null) {
                            messageModel.setMessagePlatform(MessageModel.MessagePlatform.EMAIL);
                            messageModel.setMessageType(MessageModel.MessageType.PLAIN_TEXT);
                            messageModel.setReceiver(userDetailsModel.getEmail());
                        } else if (userDetailsModel.getTelegramUsername() != null) {
                            messageModel.setMessagePlatform(MessageModel.MessagePlatform.TELEGRAM);
                            messageModel.setMessageType(MessageModel.MessageType.PLAIN_TEXT);
                            messageModel.setReceiver(userDetailsModel.getTelegramUsername());
                        }

                        messageModel.setSubject("Notification from CRM");
                        messageModel.setContent("Product " + product.getName() + " " + product.getBrand() + " has been delivered as " + product.getItemsLeft() + " items, check it out in your waiting list \\:3");

                        messageProducer.produce(messageModel);
                    }
                }
            }
        };
        scheduledExecutorService.schedule(mailingTask, 10, TimeUnit.SECONDS);
    }

    public void notifyManager(Product product) {
        Runnable mailingTask = () -> {
            List<UserDetailsModel> managers = userDetailsImplementationService.getUsersByRole(UserSecurityRole.ROLE_MANAGER);

            if (managers != null && !managers.isEmpty()) {
                for (UserDetailsModel userDetailsModel : managers) {
                    if (userDetailsModel != null) {

                        MessageModel messageModel = new MessageModel();

                        messageModel.setSender("CRM");

                        if (userDetailsModel.getEmail() != null) {
                            messageModel.setMessagePlatform(MessageModel.MessagePlatform.EMAIL);
                            messageModel.setMessageType(MessageModel.MessageType.PLAIN_TEXT);
                            messageModel.setReceiver(userDetailsModel.getEmail());
                        } else if (userDetailsModel.getTelegramUsername() != null) {
                            messageModel.setMessagePlatform(MessageModel.MessagePlatform.TELEGRAM);
                            messageModel.setMessageType(MessageModel.MessageType.PLAIN_TEXT);
                            messageModel.setReceiver(userDetailsModel.getTelegramUsername());
                        }

                        messageModel.setSubject("Notification from CRM");
                        messageModel.setContent("Product with product ID " + product.getProductId().toString().replaceAll("-", "\\\\-") + " has " + product.getItemsLeft() + " items left");

                        messageProducer.produce(messageModel);
                    }
                }
            }
        };
        scheduledExecutorService.schedule(mailingTask, 10, TimeUnit.SECONDS);
    }
}
