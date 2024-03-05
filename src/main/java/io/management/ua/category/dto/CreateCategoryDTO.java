package io.management.ua.category.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.Map;
import java.util.UUID;

@Data
public class CreateCategoryDTO {
    @NotBlank(message = "Name of category can not be blank")
    private String name;
    private UUID parentCategoryId;
    private boolean enabled = true;
    private Map<String, String> parameters;
}
