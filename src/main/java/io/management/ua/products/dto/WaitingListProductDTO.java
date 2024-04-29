package io.management.ua.products.dto;

import io.management.ua.products.attributes.Currency;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
public class WaitingListProductDTO {
    private Long id;
    private String name;
    private UUID productId;
    private BigDecimal cost;
    private Currency currency;
    private UUID categoryId;
    private String introductionPictureUrl;
}
