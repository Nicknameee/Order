package io.management.ua.products.entity;

import io.management.ua.address.converter.JsonMapConverter;
import io.management.ua.category.entity.Category;
import io.management.ua.products.model.ProductParameter;
import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data
@Entity
@Table(name = "products")
@FieldNameConstants
public class ProductModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Convert(converter = JsonMapConverter.class)
    @Column(name = "parameters")
    private Map<ProductParameter, String> parameters;
    @Column(name = "description")
    private String description;
    @Column(name = "vendor_id")
    private UUID vendorId;
    @Column(name = "product_id")
    private UUID productId;
    @Column(name = "cost")
    private BigDecimal cost;
    @Column(name = "items_left")
    private Integer itemsLeft;
    @Column(name = "blocked")
    private Boolean blocked;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "category_id", referencedColumnName = "id")
    private Category category;

    public Object getParameter(ProductParameter productParameter) {
        return parameters.get(productParameter);
    }
}
