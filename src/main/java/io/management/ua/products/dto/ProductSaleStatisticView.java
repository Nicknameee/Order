package io.management.ua.products.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;
import java.util.UUID;

@Data
public class ProductSaleStatisticView {
    private UUID productId;
    private Integer itemsSold;
    private BigDecimal totalCost;
    private Date date;
}
