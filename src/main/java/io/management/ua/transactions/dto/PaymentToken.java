package io.management.ua.transactions.dto;

import lombok.Data;

import java.io.Serializable;

@Data
public class PaymentToken implements Serializable {
    private String signature;
    private IntermediateSigningKey intermediateSigningKey;
    private String protocolVersion;
    private String signedMessage;
}
