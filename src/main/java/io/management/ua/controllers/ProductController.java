package io.management.ua.controllers;

import io.management.ua.products.dto.ProductDTO;
import io.management.ua.products.dto.ProductFilter;
import io.management.ua.products.service.ProductService;
import io.management.ua.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/allowed")
    public Response<?> getProducts(@RequestParam(value = "page", required = false) Integer page,
                                   @RequestParam(value = "size",required = false) Integer size,
                                   @RequestBody @Valid ProductFilter productFilter) {
        return Response.ok(productService.getProducts(productFilter, page, size));
    }

    @PreAuthorize("hasRole(T(io.management.ua.utility.models.UserSecurityRoles).ROLE_MANAGER)")
    @PostMapping("/save")
    public Response<?> saveProduct(@RequestBody @Valid ProductDTO productDTO) {
        return Response.ok(productService.saveProduct(productDTO));
    }

    @PreAuthorize("hasRole(T(io.management.ua.utility.models.UserSecurityRoles).ROLE_MANAGER)")
    @PostMapping("/remove")
    public Response<?> removeProduct(@RequestParam("product_id") Long productId) {
        productService.removeProduct(productId);
        return Response.ok();
    }
}
