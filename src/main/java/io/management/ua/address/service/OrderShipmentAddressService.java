package io.management.ua.address.service;

import io.management.ua.address.dto.OrderShipmentAddressDTO;
import io.management.ua.address.entity.OrderShipmentAddress;
import io.management.ua.address.mapper.OrderShipmentAddressMapper;
import io.management.ua.address.repository.OrderShipmentAddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class OrderShipmentAddressService {
    private final OrderShipmentAddressRepository orderShipmentAddressRepository;
    private final OrderShipmentAddressMapper orderShipmentAddressMapper;

    public OrderShipmentAddress saveOrderShipmentAddress(OrderShipmentAddressDTO orderShipmentAddressDTO) {
        OrderShipmentAddress orderShipmentAddress = orderShipmentAddressMapper.dtoToEntity(orderShipmentAddressDTO);

        return orderShipmentAddressRepository.save(orderShipmentAddress);
    }
}
