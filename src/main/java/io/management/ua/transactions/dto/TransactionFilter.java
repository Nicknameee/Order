package io.management.ua.transactions.dto;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class TransactionFilter {
    private ZonedDateTime issuedAtFrom;
    private ZonedDateTime issuedAtTo;
}
