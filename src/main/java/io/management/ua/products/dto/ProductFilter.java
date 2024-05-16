package io.management.ua.products.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilter {
    private String name;
    private String brand;
    private List<UUID> productIds;
    private Integer priceFrom;
    private Integer priceTo;
    private Boolean isPresent;
    private Boolean isBlocked;
    private UUID categoryId;
}
