package io.management.ua.orders.dto;

import io.management.ua.address.dto.OrderShipmentAddressDTO;
import io.management.ua.orders.attributes.PaymentType;
import io.management.ua.products.dto.OrderedProductDTO;
import io.management.ua.utility.api.enums.DeliveryServiceType;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;
import java.util.UUID;

@Data
public class CreateOrderDTO {
    @NotNull(message = "Customer ID can not be null")
    @Min(value = 1, message = "Invalid ID value")
    private Long customerId;
    @NotNull(message = "Payment type must be specified")
    private PaymentType paymentType;
    private boolean paid;
    @NotNull(message = "Delivery type must be specified")
    private DeliveryServiceType deliveryServiceType;
    @NotNull(message = "Order must have products")
    @Size(min = 1, message = "Order must have at least one ordered product")
    private List<OrderedProductDTO> orderedProducts;
    private OrderShipmentAddressDTO orderShipmentAddress;
    private UUID transactionId;
}
