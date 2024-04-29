package io.management.ua.products.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CustomerOrderedProduct {
    private int productAmount;
    private CustomerProduct product;
}
