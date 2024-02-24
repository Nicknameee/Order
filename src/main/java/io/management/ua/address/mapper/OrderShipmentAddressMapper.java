package io.management.ua.address.mapper;

import io.management.ua.address.dto.OrderShipmentAddressDTO;
import io.management.ua.address.entity.OrderShipmentAddress;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderShipmentAddressMapper {
    OrderShipmentAddress dtoToEntity(OrderShipmentAddressDTO orderShipmentAddressDTO);
}
