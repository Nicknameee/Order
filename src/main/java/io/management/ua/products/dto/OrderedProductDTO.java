package io.management.ua.products.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class OrderedProductDTO {
    @NotNull(message = "Product ID can not be null")
    private UUID productId;
    @NotNull(message = "Amount can not be null")
    @Min(value = 1, message = "At least one item must be ordered")
    private Integer amount;
}
