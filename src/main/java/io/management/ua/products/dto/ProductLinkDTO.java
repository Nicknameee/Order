package io.management.ua.products.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class ProductLinkDTO {
    private UUID productId;
    private String productName;
    private String categoryName;
}
