package io.management.ua.products.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilter {
    private String name;
    private Map<String, String> characteristics;
    private UUID vendorId;
    private List<UUID> productIds;
    @JsonProperty("price_from")
    private Integer priceFrom;
    @JsonProperty("price_to")
    private Integer priceTo;
    private Boolean isPresent;
    private Boolean isBlocked;
    private Long categoryId;
}
