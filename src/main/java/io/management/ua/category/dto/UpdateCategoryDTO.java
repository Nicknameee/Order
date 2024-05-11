package io.management.ua.category.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.UUID;

@Data
public class UpdateCategoryDTO {
    @NotNull(message = "Category ID can not be null")
    private UUID categoryId;
    @NotBlank(message = "Category name can not be blank")
    private String name;
    private UUID parentCategoryId;
    private Boolean enabled;
}
