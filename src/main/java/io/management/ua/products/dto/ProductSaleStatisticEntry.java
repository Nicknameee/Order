package io.management.ua.products.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductSaleStatisticEntry {
    private UUID productId;
    private Integer itemsSold;
    private BigDecimal totalCost;
    private UUID categoryId;
}
