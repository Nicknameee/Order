package io.management.ua.products.model;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductDeliveryModel {
    private BigDecimal weight;
    private BigDecimal width;
    private BigDecimal length;
    private BigDecimal height;
    private BigDecimal cost;
}
