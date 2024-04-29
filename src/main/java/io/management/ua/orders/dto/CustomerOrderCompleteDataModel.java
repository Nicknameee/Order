package io.management.ua.orders.dto;

import io.management.ua.transactions.dto.TransactionStateMessage;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerOrderCompleteDataModel implements Serializable {
    private CustomerOrder order;
    private TransactionStateMessage transaction;
}
