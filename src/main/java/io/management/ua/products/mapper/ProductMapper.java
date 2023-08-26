package io.management.ua.products.mapper;

import io.management.ua.products.dto.ProductDTO;
import io.management.ua.products.entity.ProductModel;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ProductMapper {
    ProductDTO entityToDTO(ProductModel productModel);
    ProductModel dtoToEntity(ProductDTO productDTO);
}
