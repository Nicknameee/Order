package io.management.ua.products.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductFilter {
    private String category;
    private String brand;
    private String name;
    @JsonProperty("price_from")
    private int priceFrom;
    @JsonProperty("price_to")
    private int priceTo = Integer.MAX_VALUE;
}
