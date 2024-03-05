package io.management.ua.orders.dto;

import io.management.ua.orders.attributes.OrderStatus;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class UpdateOrderDTO {
    @NotNull(message = "Order ID can not be null")
    @Min(value = 1, message = "Invalid ID value")
    private Long orderId;
    @NotNull(message = "Invalid next order status")
    private OrderStatus nextStatus;
}
