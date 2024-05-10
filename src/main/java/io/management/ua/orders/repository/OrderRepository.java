package io.management.ua.orders.repository;

import io.management.ua.orders.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigInteger;
import java.util.List;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Optional<Order> findByNumber(BigInteger number);

    @Query(value = "SELECT processing_operator_id " +
            "FROM orders " +
            "WHERE processing_operator_id IN :uwu " +
            "GROUP BY processing_operator_id " +
            "ORDER BY COUNT(processing_operator_id)", nativeQuery = true)
    List<Long> getListOfManagerIds(@Param("uwu") List<Long> uwu);
}
