package io.management.ua.address.entity;

import com.vladmihalcea.hibernate.type.json.JsonType;
import io.management.ua.address.attributes.AddressPart;
import lombok.Data;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.util.Map;

@Data
@Entity
@Table(name = "order_shipment_addresses")
@TypeDef(name = "JSON", typeClass = JsonType.class)
public class OrderShipmentAddress {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "order_id")
    private Long orderId;
    @Column(name = "customer_id")
    private Long customerId;
    @Type(type = "JSON")
    @Column(name = "address", columnDefinition = "JSON")
    private Map<AddressPart, String> address;
}
