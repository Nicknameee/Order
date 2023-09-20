package io.management.ua.orders.entity;

import io.management.ua.orders.attributes.OrderStatus;
import io.management.ua.products.entity.ProductModel;
import io.management.ua.utility.enums.PaymentType;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Entity
@Table(name = "orders")
@SequenceGenerator(name = "order_number_sequence", initialValue = 10000001, allocationSize = 1)
@FieldNameConstants
public class OrderModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @GeneratedValue(generator = "order_number_sequence")
    @Column(name = "order_number", unique = true)
    private Long orderNumber;
    @Column(name = "ordered_products_cost")
    private BigDecimal totalProductCost;
    @Column(name = "delivery_cost")
    private BigDecimal deliveryCost;
    @Column(name = "order_date")
    private ZonedDateTime orderDate;
    @Column(name = "order_status")
    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;
    @Column(name = "payment_type")
    @Enumerated(EnumType.STRING)
    private PaymentType paymentType;
    @Column(name = "customer_id")
    private Long customerId;
    @Column(name = "processing_operator_id")
    private Long processingOperatorId;
    @Column(name = "paid")
    private Boolean paid;
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @JoinTable(name = "ordered_products", inverseJoinColumns = @JoinColumn(name = "order_id"), joinColumns = @JoinColumn(name = "product_id"))
    private List<ProductModel> orderedProducts;
}
