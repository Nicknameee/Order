package io.management.ua.category.mapper;

import io.management.ua.category.entity.Category;
import org.mapstruct.Mapper;
import io.management.ua.category.dto.CategoryDTO;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface CategoryMapper {
    Category dtoToEntity(CategoryDTO categoryDTO);

}
