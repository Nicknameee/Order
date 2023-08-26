package io.management.ua.category.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class CategoryDTO {
    private Long id;
    @NotBlank(message = "Name of category can not be blank")
    private String name;
    private Long parentCategoryId;
}
