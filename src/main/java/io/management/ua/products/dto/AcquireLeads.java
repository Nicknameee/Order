package io.management.ua.products.dto;

import io.management.ua.products.attributes.Currency;
import lombok.Data;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
public class AcquireLeads {
    private Long customerId;
    private List<String> contact = new ArrayList<>();
    private BigDecimal totalProfitByCustomer;
    private Currency currency;
}
