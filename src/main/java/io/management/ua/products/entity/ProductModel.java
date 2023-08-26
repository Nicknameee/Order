package io.management.ua.products.entity;

import io.management.ua.category.entity.Category;
import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.UUID;

@Data
@Entity
@Table(name = "products")
public class ProductModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "product_name")
    private String productName;
    @Column(name = "product_code")
    private UUID productCode;
    @Column(name = "product_cost")
    private BigDecimal cost;
    @Column(name = "items_left")
    private Integer itemsLeft;
    @Column(name = "brand")
    private String brand;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;
}
