package io.management.ua.products.dto;

import io.management.ua.products.attributes.Currency;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CustomerProduct {
    private String name;
    private String brand;
    private UUID productId;
    private BigDecimal cost;
    private Currency currency;
}
