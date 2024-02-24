package io.management.ua.address.entity;

import io.management.ua.address.attributes.AddressPart;
import io.management.ua.address.converter.JsonMapConverter;
import lombok.Data;

import javax.persistence.*;
import java.util.Map;

@Data
@Entity
@Table(name = "order_shipment_addresses")
public class OrderShipmentAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "order_id")
    private Long orderId;
    @Column(name = "customer_id")
    private Long customerId;
    @Convert(converter = JsonMapConverter.class)
    @Column(name = "address")
    private Map<AddressPart, String> address;
}
