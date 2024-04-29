package io.management.ua.transactions.entity;

import io.management.ua.products.attributes.Currency;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.UUID;

@Data
@Entity
@Table(name = "transactions")
@FieldNameConstants
public class Transaction {
    @Id
    private UUID id;
    @Column(name = "customer_id")
    private Long customerId;
    @Column(name = "amount")
    private BigDecimal amount;
    @Column(name = "source_currency")
    @Enumerated(EnumType.STRING)
    private Currency sourceCurrency;
    @Column(name = "acquiring_currency")
    @Enumerated(EnumType.STRING)
    private Currency acquiringCurrency;
    @Column(name = "reference", unique = true)
    private BigInteger reference;
    @Column(name = "status")
    private String status;
    @Column(name = "issued_at", updatable = false)
    private ZonedDateTime issuedAt;
    @Column(name = "merchant_id")
    private String merchantId;
    @Column(name = "transaction_id")
    private String transactionId;
    @Column(name = "authorization_code")
    private String authorizationCode;
    @Column(name = "acquire_authorization_code")
    private String acquireAuthorizationCode;
    @Column(name = "transaction_processing_country")
    private String transactionProcessingCountry;
    @Column(name = "transaction_type")
    private String transactionType;
    @Column(name = "transaction_alias")
    private String transactionAlias;
    @Column(name = "expiration")
    private String expiration;
    @Column(name = "pan")
    private String pan;
    @Column(name = "settled_at")
    private ZonedDateTime settledAt;
}
