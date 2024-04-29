package io.management.ua.transactions.dto;

import io.management.ua.products.attributes.Currency;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
public class TransactionStateMessage implements Serializable {
    private Long customerId;
    private BigDecimal amount;
    private Currency sourceCurrency;
    private String status;
    private Boolean authorized;
    private Boolean settled;
}
