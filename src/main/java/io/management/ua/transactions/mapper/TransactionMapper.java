package io.management.ua.transactions.mapper;

import io.management.ua.transactions.dto.TransactionStateMessage;
import io.management.ua.transactions.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface TransactionMapper {
    TransactionStateMessage modelToStateMessage(Transaction transaction);
}
