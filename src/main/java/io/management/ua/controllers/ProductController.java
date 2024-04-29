package io.management.ua.controllers;

import io.management.ua.products.dto.CreateProductDTO;
import io.management.ua.products.dto.ProductFilter;
import io.management.ua.products.dto.UpdateProductDTO;
import io.management.ua.products.service.ProductService;
import io.management.ua.response.Response;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {
    private final ProductService productService;

    @GetMapping("/search/allowed")
    public Response<?> getProductLinksByNamePartial(@RequestParam String searchBy, @RequestParam(required = false) Integer page) {
        return Response.ok(productService.getProductLinksByNamePartial(searchBy, page));
    }

    @PostMapping("/allowed")
    public Response<?> getProducts(@RequestParam(required = false) Integer page,
                                   @RequestParam(required = false) Integer size,
                                   @RequestParam(required = false) String sortBy,
                                   @RequestParam(required = false) String direction,
                                   @RequestBody ProductFilter productFilter) {
        return Response.ok(productService.getProducts(productFilter, page, size, sortBy, direction));
    }

    @PreAuthorize("hasRole(T(io.management.ua.utility.models.UserSecurityRole).ROLE_MANAGER)")
    @PostMapping("/save")
    public Response<?> saveProduct(@RequestBody CreateProductDTO createProductDTO) {
        return Response.ok(productService.saveProduct(createProductDTO));
    }

    @PreAuthorize("hasRole(T(io.management.ua.utility.models.UserSecurityRole).ROLE_MANAGER)")
    @PostMapping("/picture")
    public Response<?> setProductPictures(@RequestParam UUID productId,
                                          @RequestParam(required = false) MultipartFile picture) {
        return Response.ok(productService.setProductIntroductionPicture(productId, picture));
    }

    @PreAuthorize("hasRole(T(io.management.ua.utility.models.UserSecurityRole).ROLE_MANAGER)")
    @PostMapping("/pictures")
    public Response<?> setProductPictures(@RequestParam UUID productId,
                                          @RequestParam(required = false) List<String> picturesToRemove,
                                          @RequestParam(required = false) List<MultipartFile> picturesToAdd) {
        return Response.ok(productService.setProductPictures(productId, picturesToRemove, picturesToAdd));
    }

    @PreAuthorize("hasRole(T(io.management.ua.utility.models.UserSecurityRole).ROLE_MANAGER)")
    @PutMapping("/update")
    public Response<?> updateProduct(@RequestBody UpdateProductDTO updateProductDTO) {
        return Response.ok(productService.updateProduct(updateProductDTO));
    }

    @PreAuthorize("hasRole(T(io.management.ua.utility.models.UserSecurityRole).ROLE_MANAGER)")
    @GetMapping("/export")
    public void exportProductData(@RequestBody(required = false) ProductFilter vendorFilter,
                                  @RequestParam(required = false) String filename,
                                  HttpServletResponse httpServletResponse) {
        productService.exportProducts(vendorFilter, filename, httpServletResponse);
    }

    @GetMapping("/amount/available")
    public Response<?> getProductAmount(@RequestParam UUID productId) {
        return Response.ok(productService.getProductAmount(productId));
    }

    @GetMapping("/waiting/list")
    public Response<?> getWaitingList(@RequestParam Long customerId) {
        return Response.ok(productService.getWaitingList(customerId));
    }

    @PostMapping("/waiting/list")
    public Response<?> addProductToWaitingList(@RequestParam Long customerId, @RequestParam UUID productId) {
        return Response.ok(productService.addProductToWaitingList(customerId, productId));
    }

    @DeleteMapping("/waiting/list")
    public Response<?> removeProductFromWaitingList(@RequestParam Long customerId, @RequestParam UUID productId) {
        return Response.ok(productService.removeProductFromWaitingList(customerId, productId));
    }
}
