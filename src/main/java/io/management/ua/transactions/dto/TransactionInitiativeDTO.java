package io.management.ua.transactions.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.management.ua.products.attributes.Currency;
import lombok.Data;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.math.BigInteger;

@Data
public class TransactionInitiativeDTO {
    @NotNull
    @DecimalMin(value = "1", message = "Invalid value for amount of transaction")
    private BigDecimal amount;
    @NotNull(message = "Currency is invalid")
    private Currency currency;
    @NotNull(message = "Invalid payment token")
    private String paymentToken;
    @NotNull(message = "Invalid customer ID")
    @Min(value = 1, message = "Customer ID value is invalid")
    private Long customerId;
    @JsonIgnore
    private BigInteger reference = new BigInteger(String.valueOf(System.currentTimeMillis()));
}
