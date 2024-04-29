package io.management.ua.orders.dto;

import io.management.ua.address.entity.OrderShipmentAddress;
import io.management.ua.orders.attributes.OrderStatus;
import io.management.ua.orders.attributes.PaymentType;
import io.management.ua.products.dto.CustomerOrderedProduct;
import io.management.ua.utility.TimeUtil;
import io.management.ua.utility.api.enums.DeliveryServiceType;
import lombok.Data;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class CustomerOrder {
    private Long id;
    private Long customerId;
    private BigDecimal deliveryCost = BigDecimal.ZERO;
    private BigInteger number = BigInteger.valueOf(TimeUtil.getCurrentDateTime().getNano());
    private BigDecimal orderedProductCost = BigDecimal.ZERO;
    private ZonedDateTime creationDate = TimeUtil.getCurrentDateTime();
    private OrderStatus status = OrderStatus.INITIATED;
    private PaymentType paymentType;
    private Long processingOperatorId;
    private Boolean paid = false;
    private ZonedDateTime lastUpdateDate;
    private List<CustomerOrderedProduct> orderedProducts;
    private OrderShipmentAddress shipmentAddress;
    private DeliveryServiceType deliveryServiceType;
    private UUID transactionId;
}
