package io.management.ua.products.entity;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;

@Data
@Entity
@Table(name = "ordered_products")
@FieldNameConstants
public class OrderedProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
    @Column(name = "products_amount")
    private int productAmount;
}
