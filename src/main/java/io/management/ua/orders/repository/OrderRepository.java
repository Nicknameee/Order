package io.management.ua.orders.repository;

import io.management.ua.orders.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;

import java.math.BigInteger;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByNumber(BigInteger number);
}
