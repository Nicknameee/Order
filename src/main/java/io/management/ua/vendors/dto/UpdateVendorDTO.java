package io.management.ua.vendors.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Data
public class UpdateVendorDTO {
    @NotBlank(message = "Vendor name can not be blank")
    private UUID vendorId;
    private String name;
    private String website;
    private String phone;
    private boolean revoke;
}
