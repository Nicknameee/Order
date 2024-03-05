package io.management.ua.vendors.dto;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class VendorFilter {
    private ZonedDateTime joiningDateFrom;
    private ZonedDateTime joiningDateTo;
    private Boolean isRevoked;
}
