package io.management.ua.orders.dto;

import io.management.ua.orders.attributes.OrderStatus;
import io.management.ua.products.dto.OrderedProductDTO;
import io.management.ua.utility.TimeUtil;
import io.management.ua.utility.enums.PaymentType;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Data
public class OrderDTO {
    private Long id;
    private Long orderNumber;
    private BigDecimal totalProductCost;
    @NotNull(message = "Delivery cost must be specified")
    private BigDecimal deliveryCost;
    private ZonedDateTime orderDate = TimeUtil.getCurrentDateTime();
    private OrderStatus orderStatus = OrderStatus.INITIATED;
    @NotNull(message = "Payment type must be specified")
    private PaymentType paymentType;
    @NotNull(message = "Customer ID can not be null")
    @Min(value = 1, message = "Invalid ID value")
    private Long customerId;
    private Long processingOperatorId;
    @NotNull(message = "Order must have products")
    @Size(min = 1, message = "Order must have at least one ordered product")
    private List<OrderedProductDTO> orderedProducts;
}
