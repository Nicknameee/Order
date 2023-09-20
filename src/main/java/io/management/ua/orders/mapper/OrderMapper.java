package io.management.ua.orders.mapper;

import io.management.ua.orders.dto.OrderDTO;
import io.management.ua.orders.entity.OrderModel;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface OrderMapper {
    OrderDTO entityToDTO(OrderModel productModel);
    OrderModel dtoToEntity(OrderDTO productDTO);
}


