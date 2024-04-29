package io.management.ua.products.service;

import io.management.resources.models.Image;
import io.management.resources.service.ImageHostingService;
import io.management.ua.annotations.DefaultNumberValue;
import io.management.ua.annotations.DefaultStringValue;
import io.management.ua.category.repository.CategoryRepository;
import io.management.ua.exceptions.DefaultException;
import io.management.ua.exceptions.NotFoundException;
import io.management.ua.orders.entity.Order;
import io.management.ua.products.dto.*;
import io.management.ua.products.entity.Product;
import io.management.ua.products.entity.WaitingListProduct;
import io.management.ua.products.mapper.ProductMapper;
import io.management.ua.products.repository.ProductRepository;
import io.management.ua.products.repository.WaitingListProductRepository;
import io.management.ua.utility.ExportUtil;
import io.management.ua.utility.ResourceLoaderUtil;
import io.management.ua.utility.Scripts;
import io.management.ua.utility.api.enums.Folder;
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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Nullable;
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
import java.util.*;

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
                                     @DefaultStringValue(string = "ASC") String direction) {
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

            if (productFilter.getVendorId() != null) {
                predicates.add(criteriaBuilder.equal(root.get(Product.Fields.vendorId), productFilter.getVendorId()));
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
                predicates.add(criteriaBuilder.equal(root.get(Product.Fields.categoryId), productFilter.getCategoryId()));
            }
        }

        query.where(predicates.toArray(Predicate[]::new));

        if (direction != null) {
            switch (Sort.Direction.valueOf(direction)) {
                case DESC -> query.orderBy(criteriaBuilder.desc(root.get(sortBy)));
                case ASC -> query.orderBy(criteriaBuilder.asc(root.get(sortBy)));
            }
        } else {
            query.orderBy(criteriaBuilder.desc(root.get(Order.Fields.creationDate)));
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

        if (updateProductDTO.getVendorId() != null) {
            product.setVendorId(updateProductDTO.getVendorId());
        }

        if (updateProductDTO.getCost() != null && updateProductDTO.getCost().compareTo(BigDecimal.ZERO) > 0) {
            product.setCost(updateProductDTO.getCost());
        }

        if (updateProductDTO.getCurrency() != null) {
            product.setCurrency(updateProductDTO.getCurrency());
        }

        if (updateProductDTO.getItemsLeft() != null && updateProductDTO.getItemsLeft() >= 0) {
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

    public Product setProductPictures(UUID productId, List<String> picturesToRemove, List<MultipartFile> picturesToAdd) {
        if ((picturesToRemove == null || picturesToRemove.isEmpty()) && (picturesToAdd == null || picturesToAdd.isEmpty())) {
            throw new DefaultException("Product pictures setting invocation is invalid");
        }

        Product product = productRepository.findByProductId(productId)
                .orElseThrow(() -> new NotFoundException("Product with product ID {} was not found", productId));

        if (picturesToRemove != null) {
            for (String url : picturesToRemove) {
                imageHostingService.deleteImage(url);
            }

            List<String> urls = new ArrayList<>(Arrays.asList(product.getPictureUrls()));
            urls.removeAll(picturesToRemove);
            product.setPictureUrls(urls.toArray(String[]::new));
        }

        if (picturesToAdd != null) {
            if (product.getPictureUrls() == null) {
                product.setPictureUrls(new String[]{});
            }

            for (MultipartFile picture : picturesToAdd) {
                Image image = imageHostingService.uploadImage(picture, Folder.PRODUCTS + product.getProductId());

                List<String> urls = new ArrayList<>(Arrays.asList(product.getPictureUrls()));
                urls.add(image.getSecureUrl());
                product.setPictureUrls(urls.toArray(String[]::new));
            }
        }

        return productRepository.save(product);
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void addProductSaleStatisticEntry(ProductSaleStatisticEntry productSaleStatisticEntry) {
        jdbcTemplate.update(ResourceLoaderUtil.getResourceContent(Scripts.addProductSaleStatisticEntry),
                productSaleStatisticEntry.getProductId(),
                productSaleStatisticEntry.getItemsSold(),
                productSaleStatisticEntry.getTotalCost(),
                productSaleStatisticEntry.getVendorId(),
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

    public void orderProduct(Long orderId, Long productId, Integer productAmount) {
        jdbcTemplate.update(ResourceLoaderUtil.getResourceContent(Scripts.addOrderedProductEntry), orderId, productId, productAmount);
    }
}
