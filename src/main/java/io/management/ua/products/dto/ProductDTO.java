package io.management.ua.products.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class ProductDTO {
    private Long id;
    private String productName;
    private UUID productCode;
    private BigDecimal cost;
    private Integer itemsLeft;
    private String brand;
    private Long categoryId;
}
