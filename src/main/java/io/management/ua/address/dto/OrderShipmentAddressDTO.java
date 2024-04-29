package io.management.ua.address.dto;

import io.management.ua.address.attributes.AddressPart;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.Map;

@Data
public class OrderShipmentAddressDTO {
    private Long id;
    private Long orderId;
    private Long customerId;
    @NotNull(message = "Invalid address")
    private Map<AddressPart, String> address;
}
