package io.management.ua.products.entity;

import com.vladmihalcea.hibernate.type.array.StringArrayType;
import com.vladmihalcea.hibernate.type.json.JsonType;
import lombok.Data;
import lombok.experimental.FieldNameConstants;
import org.hibernate.annotations.Type;
import org.hibernate.annotations.TypeDef;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data
@Entity
@Table(name = "products")
@FieldNameConstants
@TypeDef(name = "JSON", typeClass = JsonType.class)
@TypeDef(name = "stringArray", typeClass = StringArrayType.class)
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "brand")
    private String brand;
    @Type(type = "JSON")
    @Column(name = "parameters", columnDefinition = "JSON")
    private Map<String, String> parameters;
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
    @Column(name = "category_id")
    private UUID categoryId;
    @Column(name = "introduction_picture_url")
    private String introductionPictureUrl;
    @Type(type = "stringArray")
    @Column(name = "picture_urls", columnDefinition = "VARCHAR[]")
    private String[] pictureUrls;
    @Column(name = "margin_rate")
    private BigDecimal marginRate;

    public String getParameter(String productParameter) {
        return parameters.get(productParameter);
    }
}
