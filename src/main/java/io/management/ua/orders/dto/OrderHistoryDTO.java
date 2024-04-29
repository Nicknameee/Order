package io.management.ua.orders.dto;

import io.management.ua.orders.entity.Order;
import lombok.Data;

import java.sql.Timestamp;
import java.util.List;

@Data
public class OrderHistoryDTO {
    private Long orderId;
    private Order oldOrder;
    private Order updatedOrder;
    private Long iteration;
    private List<String> updatedFields;
    private Timestamp updateTime;
}
