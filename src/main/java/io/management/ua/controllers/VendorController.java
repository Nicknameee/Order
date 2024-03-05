package io.management.ua.controllers;

import io.management.ua.response.Response;
import io.management.ua.vendors.dto.CreateVendorDTO;
import io.management.ua.vendors.dto.UpdateVendorDTO;
import io.management.ua.vendors.dto.VendorFilter;
import io.management.ua.vendors.service.VendorService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/vendors")
@RequiredArgsConstructor
public class VendorController {
    private final VendorService vendorService;

    @GetMapping("/allowed")
    public Response<?> getVendors(@RequestBody(required = false) VendorFilter vendorFilter,
                                  @RequestParam(required = false) Integer page,
                                  @RequestParam(required = false) Integer size) {
        return Response.ok(vendorService.getVendors(vendorFilter, page, size));
    }

    @GetMapping("/personal/allowed")
    public Response<?> getVendor(@RequestParam UUID vendorId) {
        return Response.ok(vendorService.getVendorById(vendorId));
    }

    @PostMapping("/save")
    public Response<?> saveVendor(@RequestBody CreateVendorDTO createVendorDTO) {
        return Response.ok(vendorService.saveVendor(createVendorDTO));
    }

    @PostMapping("/picture")
    public Response<?> setVendorPicture(@RequestParam UUID vendorId,
                                        @RequestParam(required = false) MultipartFile picture) {
        return Response.ok(vendorService.setVendorPicture(vendorId, picture));
    }

    @PutMapping("/update")
    public Response<?> updateVendor(@RequestBody UpdateVendorDTO updateVendorDTO) {
        return Response.ok(vendorService.updateVendor(updateVendorDTO));
    }

    @GetMapping("/export")
    public void exportVendorData(@RequestBody(required = false) VendorFilter vendorFilter,
                                 @RequestParam(required = false) String filename,
                                 HttpServletResponse httpServletResponse) {
        vendorService.exportVendors(vendorFilter, filename, httpServletResponse);
    }
}
