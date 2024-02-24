package io.management.ua.address.repository;

import io.management.ua.address.entity.OrderShipmentAddress;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderShipmentAddressRepository extends JpaRepository<OrderShipmentAddress, Long> {
}
