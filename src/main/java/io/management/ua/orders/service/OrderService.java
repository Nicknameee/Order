package io.management.ua.orders.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.management.ua.address.dto.OrderShipmentAddressDTO;
import io.management.ua.address.entity.OrderShipmentAddress;
import io.management.ua.address.service.OrderShipmentAddressService;
import io.management.ua.annotations.DefaultNumberValue;
import io.management.ua.annotations.DefaultStringValue;
import io.management.ua.exceptions.ActionRestrictedException;
import io.management.ua.exceptions.DefaultException;
import io.management.ua.exceptions.NotFoundException;
import io.management.ua.orders.attributes.OrderStatus;
import io.management.ua.orders.attributes.PaymentType;
import io.management.ua.orders.dto.CreateOrderDTO;
import io.management.ua.orders.dto.OrderFilter;
import io.management.ua.orders.dto.OrderHistoryDTO;
import io.management.ua.orders.dto.UpdateOrderDTO;
import io.management.ua.orders.entity.Order;
import io.management.ua.orders.mapper.OrderMapper;
import io.management.ua.orders.repository.OrderRepository;
import io.management.ua.products.dto.OrderedProductDTO;
import io.management.ua.products.dto.ProductSaleStatisticEntry;
import io.management.ua.products.entity.Product;
import io.management.ua.products.service.ProductService;
import io.management.ua.utility.*;
import io.management.ua.utility.api.enums.DeliveryServiceType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.*;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
@Validated
public class OrderService {
    @PersistenceContext(unitName = "database")
    private final EntityManager entityManager;
    private final JdbcTemplate jdbcTemplate;
    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final ProductService productService;
    private final DeliveryService deliveryService;
    private final OrderShipmentAddressService orderShipmentAddressService;

    @Transactional(readOnly = true)
    public List<Order> getOrders(OrderFilter orderFilter,
                                 @DefaultNumberValue Integer page,
                                 @DefaultNumberValue(number = 100) Integer size,
                                 @DefaultStringValue(string = Order.Fields.creationDate) String sortBy,
                                 @DefaultStringValue(string = "ASC") String direction) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Order> query = criteriaBuilder.createQuery(Order.class);
        Root<Order> root = query.from(Order.class);

        List<Predicate> predicates = new ArrayList<>();

        if (orderFilter != null) {
            if (orderFilter.getId() != null && orderFilter.getId() > 0) {
                predicates.add(criteriaBuilder.equal(root.get(Order.Fields.id), orderFilter.getId()));
            }

            if (orderFilter.getCustomerId() != null && orderFilter.getCustomerId() > 0) {
                predicates.add(criteriaBuilder.equal(root.get(Order.Fields.customerId), orderFilter.getCustomerId()));
            }

            if (orderFilter.getTotalDeliveryCostFrom() != null && orderFilter.getTotalDeliveryCostFrom().compareTo(BigDecimal.ZERO) > 0) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(Order.Fields.deliveryCost), orderFilter.getTotalDeliveryCostFrom()));
            }

            if (orderFilter.getTotalDeliveryCostTo() != null && orderFilter.getTotalDeliveryCostTo().compareTo(BigDecimal.ZERO) > 0) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(Order.Fields.deliveryCost), orderFilter.getTotalDeliveryCostTo()));
            }

            if (orderFilter.getOrderNumber() != null) {
                predicates.add(criteriaBuilder.equal(root.get(Order.Fields.number), orderFilter.getOrderNumber()));
            }

            if (orderFilter.getOrderedProductsCostFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(Order.Fields.orderedProductCost), orderFilter.getOrderedProductsCostFrom()));
            }

            if (orderFilter.getOrderedProductsCostTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(Order.Fields.orderedProductCost), orderFilter.getOrderedProductsCostTo()));
            }

            if (orderFilter.getOrderDateFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get(Order.Fields.creationDate), orderFilter.getOrderDateFrom()));
            }

            if (orderFilter.getOrderDateTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get(Order.Fields.creationDate), orderFilter.getOrderDateTo()));
            }

            if (orderFilter.getOrderStatuses() != null && !orderFilter.getOrderStatuses().isEmpty()) {
                predicates.add(root.get(Order.Fields.status).in(orderFilter.getOrderStatuses()));
            }

            if (orderFilter.getPaymentTypes() != null && !orderFilter.getPaymentTypes().isEmpty()) {
                predicates.add(root.get(Order.Fields.paymentType).in(orderFilter.getPaymentTypes()));
            }

            if (orderFilter.getProcessingOperatorIds() != null && !orderFilter.getProcessingOperatorIds().isEmpty()) {
                predicates.add(root.get(Order.Fields.processingOperatorId).in(orderFilter.getProcessingOperatorIds()));
            }

            if (orderFilter.getPaid() != null) {
                if (orderFilter.getPaid()) {
                    predicates.add(criteriaBuilder.isTrue(root.get(Order.Fields.paid)));
                } else {
                    predicates.add(criteriaBuilder.isFalse(root.get(Order.Fields.paid)));
                }
            }

            if (orderFilter.getOrderedProductIds() != null && !orderFilter.getOrderedProductIds().isEmpty()) {
                Join<Order, List<Product>> joinProducts = root.join(Order.Fields.orderedProducts);
                predicates.add(joinProducts.get(Product.Fields.productId).in(orderFilter.getOrderedProductIds()));
            }

            if (orderFilter.getProductNames() != null && !orderFilter.getProductNames().isEmpty()) {
                Join<Order, List<Product>> joinProducts = root.join(Order.Fields.orderedProducts);
                predicates.add(joinProducts.get(Product.Fields.name).in(orderFilter.getProductNames()));
            }

            if (orderFilter.getVendorIds() != null && !orderFilter.getVendorIds().isEmpty()) {
                Join<Order, List<Product>> joinProducts = root.join(Order.Fields.orderedProducts);
                predicates.add(joinProducts.get(Product.Fields.name).in(orderFilter.getVendorIds()));
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

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Order with ID: %s was not found", id)));
    }

    /**
     * Order Processing Logic
     * For each unique product item new order will be created
     * Reasons: customer can select different products from different vendors, with different delivery settings, for correct processing they are going to be split
     */
    @Transactional
    public List<Order> saveOrder(@Valid CreateOrderDTO createOrderDTO) {
        if (createOrderDTO.getOrderedProducts() == null || createOrderDTO.getOrderedProducts().isEmpty()) {
            throw new RuntimeException("Invalid order model, no ordered products");
        }

        if (createOrderDTO.getPaymentType() == PaymentType.COD && createOrderDTO.isPaid()) {
            throw new DefaultException("Invalid parameters combination, order payment type can not be {} with order paid status {}",
                    createOrderDTO.getPaymentType(), true);
        }

        if (createOrderDTO.getDeliveryServiceType() == null) {
            createOrderDTO.setDeliveryServiceType(DeliveryServiceType.NONE);
        }

        List<Order> orders = new ArrayList<>();

        for (OrderedProductDTO orderedProductDTO : createOrderDTO.getOrderedProducts()) {
            Order order = orderMapper.dtoToEntity(createOrderDTO);
            if (!createOrderDTO.isPaid()) {
                order.setStatus(OrderStatus.INITIATED);
            } else {
                order.setStatus(OrderStatus.PAID);
            }

            order = save(order);

            Pair<Product, Integer> orderedProduct = productService.getProductOrderingPair(orderedProductDTO);

            order.setOrderedProducts(new ArrayList<>(List.of(orderedProduct.getFirst())));

            BigDecimal productCost = orderedProduct.getFirst().getCost()
                    .multiply(BigDecimal.valueOf(orderedProduct.getSecond()));

            if (createOrderDTO.getDeliveryServiceType() != DeliveryServiceType.NONE) {
                BigDecimal deliveryCost =
                        deliveryService.getDeliveryCost(orderedProduct, createOrderDTO.getDeliveryServiceType(), createOrderDTO.getOrderShipmentAddress());
                order.setDeliveryCost(deliveryCost);
            }

            order.setOrderedProductCost(productCost);

            if (createOrderDTO.getDeliveryServiceType() != DeliveryServiceType.NONE) {
                OrderShipmentAddressDTO orderShipmentAddressDTO = createOrderDTO.getOrderShipmentAddress();
                orderShipmentAddressDTO.setCustomerId(order.getCustomerId());
                orderShipmentAddressDTO.setOrderId(order.getId());

                OrderShipmentAddress orderShipmentAddress = orderShipmentAddressService.saveOrderShipmentAddress(orderShipmentAddressDTO);
                order.setShipmentAddress(orderShipmentAddress);
            }

            ProductSaleStatisticEntry entry = new ProductSaleStatisticEntry();

            entry.setProductId(orderedProduct.getFirst().getProductId());
            entry.setItemsSold(orderedProduct.getSecond());
            entry.setTotalCost(productCost);
            entry.setVendorId(orderedProduct.getFirst().getVendorId());
            entry.setCategoryId(orderedProduct.getFirst().getCategoryId());

            productService.addProductSaleStatisticEntry(entry);

            order = save(order);

            orders.add(order);
        }

        return orders;
    }

    @Transactional
    public Order updateOrder(@Valid UpdateOrderDTO updateOrderDTO) {
        Order order = getOrderById(updateOrderDTO.getOrderId());

        if (updateOrderDTO.getNextStatus() == order.getStatus()) {
            log.debug("Order status update is redundant, new status {} is already registered", updateOrderDTO.getNextStatus());
            return order;
        }

        Order copy = copyOrderModelForHistoryEntryComparison(order);

        if (OrderStatus.checkTransitionRule(order.getStatus(), updateOrderDTO.getNextStatus(), order.getPaymentType())) {
            order.setStatus(updateOrderDTO.getNextStatus());
            if (updateOrderDTO.getNextStatus() == OrderStatus.PAID) {
                order.setPaid(true);
            }

            order = save(order);
        } else {
            throw new ActionRestrictedException(String.format("Order can not be transferred to the status named: %s", updateOrderDTO.getNextStatus()));
        }

        addOrderHistoryEntry(copy, order);

        return order;
    }

    private Order save(Order order) {
        order.setLastUpdateDate(TimeUtil.getCurrentDateTime());

        return orderRepository.save(order);
    }

    private List<String> analyseOrderPropertiesUpdates(Order oldOrder, Order updatedOrder) {
        List<String> updatedParams = new ArrayList<>();

        if (oldOrder == updatedOrder) {
            throw new RuntimeException("Order history entry can not arise from different orders");
        }
        if (!Objects.equals(oldOrder.getId(), updatedOrder.getId())) {
            throw new RuntimeException("Order history entry can not arise from different orders");
        }
        if (!Objects.equals(oldOrder.getCustomerId(), updatedOrder.getCustomerId())) {
            throw new RuntimeException("Order history entry can not arise from different orders");
        }
        if (!Objects.equals(oldOrder.getDeliveryCost(), updatedOrder.getDeliveryCost())) {
            updatedParams.add(Order.Fields.deliveryCost);
        }
        if (!Objects.equals(oldOrder.getNumber(), updatedOrder.getNumber())) {
            throw new RuntimeException("Order history entry can not arise from different orders");
        }
        if (!Objects.equals(oldOrder.getOrderedProductCost(), updatedOrder.getOrderedProductCost())) {
            updatedParams.add(Order.Fields.orderedProductCost);
        }
        if (oldOrder.getStatus() != updatedOrder.getStatus()) {
            updatedParams.add(Order.Fields.status);
        }
        if (oldOrder.getPaymentType() != updatedOrder.getPaymentType()) {
            updatedParams.add(Order.Fields.paymentType);
        }
        if (!Objects.equals(oldOrder.getProcessingOperatorId(), updatedOrder.getProcessingOperatorId())) {
            updatedParams.add(Order.Fields.processingOperatorId);
        }
        if (oldOrder.getPaid() != updatedOrder.getPaid()) {
            updatedParams.add(Order.Fields.paid);
        }
        if (oldOrder.getShipmentAddress() != updatedOrder.getShipmentAddress()) {
            updatedParams.add(Order.Fields.shipmentAddress);
        }
        if (oldOrder.getDeliveryServiceType() != updatedOrder.getDeliveryServiceType()) {
            updatedParams.add(Order.Fields.deliveryServiceType);
        }

        return updatedParams;
    }

    private void addOrderHistoryEntry(Order oldOrder, Order updatedOrder) {
        List<String> updatedFields = analyseOrderPropertiesUpdates(oldOrder, updatedOrder);

        jdbcTemplate.update(ResourceLoaderUtil.getResourceContent(Scripts.addOrderHistoryEntry),
                preparedStatement -> {
            preparedStatement.setLong(1, updatedOrder.getId());
            try {
                preparedStatement.setString(2, UtilManager.objectMapper().writeValueAsString(oldOrder));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            try {
                preparedStatement.setString(3, UtilManager.objectMapper().writeValueAsString(updatedOrder));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }
            preparedStatement.setLong(4, updatedOrder.getId());
            preparedStatement.setArray(5,
                    Objects.requireNonNull(jdbcTemplate.getDataSource()).getConnection().createArrayOf("VARCHAR", updatedFields.toArray()));
                });
    }

    @Transactional
    public Map<Long, List<OrderHistoryDTO>> getOrderHistory(@DefaultNumberValue Integer page,
                                                            @DefaultNumberValue(number = 100) Integer size,
                                                            @DefaultStringValue(string = Order.Fields.creationDate) String sortBy,
                                                            @DefaultStringValue(string = "ASC")String direction,
                                                            OrderFilter orderFilter) {
        List<Order> orders = getOrders(orderFilter, page, size, sortBy, direction);

        String script = ResourceLoaderUtil.getResourceContent(Scripts.getOrderHistoryEntries);

        Map<Long, List<OrderHistoryDTO>> ordersHistoryEntries = new HashMap<>();
        ObjectMapper objectMapper = UtilManager.objectMapper();

        for (Order order : orders) {
            jdbcTemplate.query(script, resultSet -> {
                try {
                    OrderHistoryDTO orderHistoryDTO = new OrderHistoryDTO();
                    orderHistoryDTO.setOrderId(resultSet.getLong("order_id"));
                    orderHistoryDTO.setOldOrder(objectMapper.readValue(resultSet.getString("old_order"), Order.class));
                    orderHistoryDTO.setUpdatedOrder(objectMapper.readValue(resultSet.getString("updated_order"), Order.class));
                    orderHistoryDTO.setIteration(resultSet.getLong("iteration"));
                    orderHistoryDTO.setUpdatedFields(Arrays.asList((String[]) resultSet.getArray("updated_fields").getArray()));

                    ordersHistoryEntries.putIfAbsent(orderHistoryDTO.getOrderId(), new ArrayList<>());

                    ordersHistoryEntries.get(orderHistoryDTO.getOrderId()).add(orderHistoryDTO);
                } catch (Exception ignored) {

                }
            },
                    order.getId());
        }

        return ordersHistoryEntries;
    }

    /**
     *
     * @param source order model before update
     * @return copy of source order model(not deep copying)
     */
    private Order copyOrderModelForHistoryEntryComparison(Order source) {
        Order copy = new Order();
        copy.setId(source.getId());
        copy.setCustomerId(source.getCustomerId());
        copy.setDeliveryCost(source.getDeliveryCost());
        copy.setNumber(source.getNumber());
        copy.setOrderedProductCost(source.getOrderedProductCost());
        copy.setCreationDate(source.getCreationDate());
        copy.setStatus(source.getStatus());
        copy.setPaymentType(source.getPaymentType());
        copy.setProcessingOperatorId(source.getProcessingOperatorId());
        copy.setPaid(source.getPaid());
        copy.setLastUpdateDate(source.getLastUpdateDate());
        copy.setOrderedProducts(source.getOrderedProducts());
        copy.setShipmentAddress(source.getShipmentAddress());
        copy.setDeliveryServiceType(source.getDeliveryServiceType());

        return copy;
    }

    @Transactional(readOnly = true)
    public void exportOrders(OrderFilter orderFilter, String filename, HttpServletResponse httpServletResponse) {
        try {
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("Products");
            sheet.setDefaultColumnWidth(50);

            httpServletResponse.setContentType(MediaType.APPLICATION_OCTET_STREAM_VALUE);
            httpServletResponse.setHeader(HttpHeaders.CONTENT_DISPOSITION, String.format("attachment; filename=%s.xlsx", ExportUtil.validateFilename(filename)));
            OutputStream outputStream = httpServletResponse.getOutputStream();

            int page = 1;
            int limitPerPage = 500;
            List<Order> orders;

            do {
                orders = getOrders(orderFilter, page, limitPerPage, null, null);

                if (!orders.isEmpty()) {
                    Row headerRow = sheet.createRow(0);
                    Field[] fields = orders.get(0).getClass().getDeclaredFields();
                    for (int i = 0; i < fields.length; i++) {
                        headerRow.createCell(i).setCellValue(ExportUtil.convertFieldNameToTitle(fields[i].getName()));
                    }

                    int rowNum = 1;

                    for (Order order : orders) {
                        Row row = sheet.createRow(rowNum++);
                        int colNum = 0;
                        for (Field field : fields) {
                            field.setAccessible(true);
                            try {
                                Object value = field.get(order);
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
            } while (!orders.isEmpty());

            workbook.close();
            outputStream.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            throw new DefaultException("Exception occurred while exporting");
        }
    }
}
