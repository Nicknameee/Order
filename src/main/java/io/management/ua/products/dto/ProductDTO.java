package io.management.ua.products.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data
public class ProductDTO {
    private String name;
    private Map<String, String> characteristics;
    private String description;
    private UUID vendorId;
    private UUID productId;
    private BigDecimal cost;
    private Integer itemsLeft;
    private Boolean blocked;
    private Long categoryId;
}
