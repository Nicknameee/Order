package io.management.ua.orders.dto;

import io.management.ua.orders.attributes.OrderStatus;
import io.management.ua.utility.enums.PaymentType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Data
public class OrderFilter {
    private Long orderId;
    private Long orderNumber;
    private BigDecimal totalProductCostFrom;
    private BigDecimal totalProductCostTo;
    private BigDecimal totalDeliveryCostFrom;
    private BigDecimal totalDeliveryCostTo;
    private ZonedDateTime orderDateFrom;
    private ZonedDateTime orderDateTo;
    private List<OrderStatus> orderStatuses;
    private PaymentType paymentType;
    private Long customerId;
    private Long processingOperatorId;
    private Boolean paid;
    private List<Long> orderedProducts;
}
