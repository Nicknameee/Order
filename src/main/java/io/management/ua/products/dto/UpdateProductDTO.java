package io.management.ua.products.dto;

import io.management.ua.products.attributes.Currency;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data
public class UpdateProductDTO {
    @NotNull(message = "Product ID can not be null")
    private UUID productId;
    private String name;
    private String brand;
    private Map<String, String> parameters;
    private String description;
    private UUID vendorId;
    private BigDecimal cost;
    private Currency currency;
    private Integer itemsLeft;
    private Boolean blocked;
    private UUID categoryId;
    private BigDecimal marginRate;
}
