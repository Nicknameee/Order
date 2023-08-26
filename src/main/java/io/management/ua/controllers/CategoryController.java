package io.management.ua.controllers;


import io.management.ua.category.dto.CategoryDTO;
import io.management.ua.category.service.CategoryService;
import io.management.ua.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/allowed")
    public Response<?> getCategories() {
        return Response.ok(categoryService.getAllCategories());
    }

    @PreAuthorize("hasRole(T(io.management.ua.utility.models.UserSecurityRoles).ROLE_MANAGER)")
    @PostMapping("/save")
    public Response<?> saveCategory(@RequestBody @Valid CategoryDTO categoryDTO) {
        return Response.ok(categoryService.saveCategory(categoryDTO));
    }

    @PreAuthorize("hasRole(T(io.management.ua.utility.models.UserSecurityRoles).ROLE_MANAGER)")
    @DeleteMapping("/remove")
    public Response<?> removeCategory(@RequestParam("category_id") Long categoryId) {
        categoryService.deleteCategory(categoryId);
        return Response.ok();
    }
}
