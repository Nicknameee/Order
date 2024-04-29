package io.management.ua.controllers;

import io.management.ua.category.dto.CreateCategoryDTO;
import io.management.ua.category.dto.UpdateCategoryDTO;
import io.management.ua.category.service.CategoryService;
import io.management.ua.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/allowed")
    public Response<?> getCategories(@RequestParam(required = false) Integer page,
                                     @RequestParam(required = false) Integer size,
                                     @RequestParam(required = false) String sortBy,
                                     @RequestParam(required = false) String direction,
                                     @RequestParam(required = false) Boolean enabled,
                                     @RequestParam(required = false) UUID parentCategoryId) {
        return Response.ok(categoryService.getAllCategories(page, size, sortBy, direction, enabled, parentCategoryId));
    }

    @PreAuthorize("hasRole(T(io.management.ua.utility.models.UserSecurityRole).ROLE_MANAGER)")
    @PostMapping("/save")
    public Response<?> saveCategory(@RequestBody CreateCategoryDTO createCategoryDTO) {
        return Response.ok(categoryService.saveCategory(createCategoryDTO));
    }

    @PreAuthorize("hasRole(T(io.management.ua.utility.models.UserSecurityRole).ROLE_MANAGER)")
    @PostMapping("/picture")
    public Response<?> setCategoryPicture(@RequestParam UUID categoryId,
                                          @RequestParam(required = false) MultipartFile picture) {
        return Response.ok(categoryService.setCategoryPicture(categoryId, picture));
    }

    @PreAuthorize("hasRole(T(io.management.ua.utility.models.UserSecurityRole).ROLE_MANAGER)")
    @PutMapping("/update")
    public Response<?> updateCategory(@RequestBody UpdateCategoryDTO updateCategoryDTO) {
        return Response.ok(categoryService.updateCategory(updateCategoryDTO));
    }

    @PreAuthorize("hasRole(T(io.management.ua.utility.models.UserSecurityRole).ROLE_MANAGER)")
    @PutMapping("/switch/state")
    public Response<?> switchCategoryState(@RequestParam UUID categoryId, @RequestParam Boolean state) {
        return Response.ok(categoryService.switchCategoryState(categoryId, state));
    }

    @PreAuthorize("hasRole(T(io.management.ua.utility.models.UserSecurityRole).ROLE_MANAGER)")
    @DeleteMapping("/remove")
    public Response<?> removeCategory(@RequestParam UUID categoryId) {
        categoryService.deleteCategory(categoryId);
        return Response.ok();
    }
}
