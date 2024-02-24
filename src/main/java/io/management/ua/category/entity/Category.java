package io.management.ua.category.entity;

import lombok.Data;
import lombok.experimental.FieldNameConstants;

import javax.persistence.*;

@Data
@Entity
@Table(name = "category")
@FieldNameConstants
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "name")
    private String name;
    @Column(name = "parent_category_id")
    private Long parentCategoryId;
    @Column(name = "picture_url")
    private String pictureUrl;
}
