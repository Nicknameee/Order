package io.management.ua.transactions.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class IntermediateSigningKey implements Serializable {
    private String signedKey;
    private List<String> signatures;
}
