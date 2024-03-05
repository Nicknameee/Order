package io.management.ua.address.dto;

import io.management.ua.address.attributes.AddressPart;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
public class OrderShipmentAddressDTO {
    private Long id;
    @NotNull
    @Min(value = 1, message = "Invalid order ID")
    private Long orderId;
    @NotNull
    @Min(value = 1, message = "Invalid customer ID")
    private Long customerId;
    private Map<AddressPart, String> address;
}
