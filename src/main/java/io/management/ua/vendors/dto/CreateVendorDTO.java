package io.management.ua.vendors.dto;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
@FieldNameConstants
public class CreateVendorDTO {
    @NotBlank(message = "Vendor name can not be blank")
    private String name;
    private String website;
    private String phone;
    @NotBlank(message = "Email can not be blank")
    @Pattern(regexp = io.management.ua.utility.Pattern.EMAIL, message = "Email is invalid")
    private String email;
}
