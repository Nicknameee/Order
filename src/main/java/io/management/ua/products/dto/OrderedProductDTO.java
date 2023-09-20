package io.management.ua.products.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
public class OrderedProductDTO {
    @NotNull(message = "Product ID can not be null")
    @Min(value = 1, message = "Invalid ID value")
    private Long id;
    @NotNull(message = "Amount can not be null")
    @Min(value = 1, message = "At least one item must be ordered")
    private Integer amount;
}
