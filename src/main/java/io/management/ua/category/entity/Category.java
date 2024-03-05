package io.management.ua.category.entity;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;
import java.util.UUID;

@Data
@Entity
@Table(name = "categories")
@FieldNameConstants
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name", unique = true)
    private String name;
    @Column(name = "category_id")
    private UUID categoryId;
    @Column(name = "parent_category_id")
    private UUID parentCategoryId;
    @Column(name = "picture_url")
    private String pictureUrl;
    @Column(name = "enabled")
    private Boolean enabled;
}
