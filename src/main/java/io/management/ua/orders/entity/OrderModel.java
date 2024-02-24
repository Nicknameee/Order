package io.management.ua.orders.entity;

import io.management.ua.address.entity.OrderShipmentAddress;
import io.management.ua.orders.attributes.OrderStatus;
import io.management.ua.orders.attributes.PaymentType;
import io.management.ua.products.entity.ProductModel;
import io.management.ua.utility.api.enums.DeliveryServiceType;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
@SequenceGenerator(name = "number_sequence", initialValue = 10000001, allocationSize = 1)
@FieldNameConstants
public class OrderModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "customer_id")
    private Long customerId;
    @Column(name = "delivery_cost")
    private BigDecimal deliveryCost;
    @GeneratedValue(generator = "number_sequence")
    @Column(name = "number", unique = true)
    private BigInteger number;
    @Column(name = "ordered_products_cost")
    private BigDecimal orderedProductCost;
    @Column(name = "creation_date")
    private ZonedDateTime creationDate;
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OrderStatus status;
    @Column(name = "payment_type")
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;
    @Column(name = "processing_operator_id")
    private Long processingOperatorId;
    @Column(name = "paid")
    private Boolean paid;
    @Column(name = "last_update_date")
    private ZonedDateTime lastUpdateDate;
    @Column(name = "shipment_address_id")
    private Long shipmentAddressId;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "ordered_products", inverseJoinColumns = @JoinColumn(name = "order_id"), joinColumns = @JoinColumn(name = "product_id"))
    private List<ProductModel> orderedProducts;
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinTable(name = "order_shipment_addresses", inverseJoinColumns = @JoinColumn(name = "order_id"), joinColumns = @JoinColumn(name = "shipment_address_id"))
    private OrderShipmentAddress shipmentAddress;
    @Column(name = "delivery_service_type")
    @Enumerated(EnumType.STRING)
    private DeliveryServiceType deliveryServiceType;
}
