package io.management.ua.transactions.repository;

import io.management.ua.transactions.entity.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.Optional;
import java.util.UUID;

public interface TransactionRepository extends JpaRepository<Transaction, UUID> {
    Optional<Transaction> findByReference(BigInteger reference);
}
