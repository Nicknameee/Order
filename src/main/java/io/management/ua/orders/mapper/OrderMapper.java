package io.management.ua.orders.mapper;

import io.management.ua.orders.dto.CreateOrderDTO;
import io.management.ua.orders.dto.CustomerOrder;
import io.management.ua.orders.entity.Order;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {
    @Mapping(target = "orderedProducts", ignore = true)
    @Mapping(target = "shipmentAddress", ignore = true)
    Order dtoToEntity(CreateOrderDTO productDTO);
    CustomerOrder entityToCustomerOrder(Order order);
}


