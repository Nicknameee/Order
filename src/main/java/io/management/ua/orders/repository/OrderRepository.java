package io.management.ua.orders.repository;

import io.management.ua.orders.entity.OrderModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrderRepository extends JpaRepository<OrderModel, Long> {
    Page<OrderModel> getOrderModelsByIdIsNotNull(Pageable pageable);
    Page<OrderModel> getOrderModelsByCustomerId(Long customerId, Pageable pageable);

     Optional<OrderModel> getOrderModelByOrderNumber(Long orderNumber);
}
