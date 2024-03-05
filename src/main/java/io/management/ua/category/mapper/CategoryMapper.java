package io.management.ua.category.mapper;

import io.management.ua.category.dto.CreateCategoryDTO;
import io.management.ua.category.entity.Category;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {
    Category dtoToEntity(CreateCategoryDTO createCategoryDTO);

}
