package io.management.ua.transactions.dto;

import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Data
public class TransactionManualInitiativeModel {
    @NotNull(message = "Order number can not be null")
    private String number;
    @NotNull(message = "Payment amount can not be null")
    @DecimalMin(value = "0", inclusive = false, message = "Amount can not be 0 or less")
    private BigDecimal paymentAmount;
}
