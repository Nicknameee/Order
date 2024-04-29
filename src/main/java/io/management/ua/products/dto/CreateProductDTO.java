package io.management.ua.products.dto;

import io.management.ua.products.attributes.Currency;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data
public class CreateProductDTO {
    @NotBlank(message = "Product name can not be blank")
    private String name;
    @NotBlank(message = "Product brand can not be blank")
    private String brand;
    private Map<String, String> parameters;
    private String description;
    @NotNull(message = "Vendor for product must be specified")
    private UUID vendorId;
    private BigDecimal cost;
    @NotNull(message = "Currency must be specified")
    private Currency currency;
    @NotNull(message = "Items number can not be null")
    @Min(value = 0, message = "Items number can not be negative")
    private Integer itemsLeft;
    private boolean blocked;
    @NotNull(message = "Category for product must be specified")
    private UUID categoryId;
    @NotNull(message = "Margin rate should be specified")
    @DecimalMin(value = "1", inclusive = false, message = "Invalid margin rate, no profit for product sale would be consumed")
    private BigDecimal marginRate;
}
