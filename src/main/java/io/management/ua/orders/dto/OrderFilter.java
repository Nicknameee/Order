package io.management.ua.orders.dto;

import io.management.ua.orders.attributes.OrderStatus;
import io.management.ua.orders.attributes.PaymentType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class OrderFilter {
    private Long id;
    private Long customerId;
    private BigDecimal totalDeliveryCostFrom;
    private BigDecimal totalDeliveryCostTo;
    private String orderNumber;
    private BigDecimal orderedProductsCostFrom;
    private BigDecimal orderedProductsCostTo;
    private ZonedDateTime orderDateFrom;
    private ZonedDateTime orderDateTo;
    private List<OrderStatus> orderStatuses;
    private List<PaymentType> paymentTypes;
    private List<Long> processingOperatorIds;
    private Boolean paid;
    private ZonedDateTime lastChangedOrderDateFrom;
    private ZonedDateTime lastChangedOrderDateTo;
    private List<Long> orderedProductIds;
    private List<String> productNames;
    private List<UUID> vendorIds;
}
