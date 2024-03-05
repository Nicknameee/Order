package io.management.ua.products.mapper;

import io.management.ua.products.dto.CreateProductDTO;
import io.management.ua.products.entity.Product;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {
    CreateProductDTO entityToDTO(Product product);
    Product dtoToEntity(CreateProductDTO createProductDTO);
}
