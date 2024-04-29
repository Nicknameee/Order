package io.management.ua.orders.entity;

import io.management.ua.address.entity.OrderShipmentAddress;
import io.management.ua.orders.attributes.OrderStatus;
import io.management.ua.orders.attributes.PaymentType;
import io.management.ua.products.entity.OrderedProduct;
import io.management.ua.utility.TimeUtil;
import io.management.ua.utility.api.enums.DeliveryServiceType;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "orders")
@FieldNameConstants
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "customer_id")
    private Long customerId;
    @Column(name = "delivery_cost")
    private BigDecimal deliveryCost = BigDecimal.ZERO;
    @Column(name = "number", unique = true)
    private BigInteger number = BigInteger.valueOf(TimeUtil.getCurrentDateTime().getNano());
    @Column(name = "ordered_products_cost")
    private BigDecimal orderedProductCost = BigDecimal.ZERO;
    @Column(name = "creation_date")
    private ZonedDateTime creationDate = TimeUtil.getCurrentDateTime();
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private OrderStatus status = OrderStatus.INITIATED;
    @Column(name = "payment_type")
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;
    @Column(name = "processing_operator_id")
    private Long processingOperatorId;
    @Column(name = "paid")
    private Boolean paid = false;
    @Column(name = "last_update_date")
    private ZonedDateTime lastUpdateDate;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinColumn(name = "order_id", referencedColumnName = "id")
    private List<OrderedProduct> orderedProducts;
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id")
    private OrderShipmentAddress shipmentAddress;
    @Column(name = "delivery_service_type")
    @Enumerated(EnumType.STRING)
    private DeliveryServiceType deliveryServiceType;
    @Column(name = "transaction_id")
    private UUID transactionId;
}
