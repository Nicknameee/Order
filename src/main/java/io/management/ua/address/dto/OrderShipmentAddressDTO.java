package io.management.ua.address.dto;

import io.management.ua.address.attributes.AddressPart;
import io.management.ua.utility.api.enums.DeliveryServiceType;
import lombok.Data;

import java.util.Map;

@Data
public class OrderShipmentAddressDTO {
    private Long id;
    private Long orderId;
    private Long customerId;
    private Map<AddressPart, String> address;
}
