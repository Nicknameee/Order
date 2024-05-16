package io.management.ua.category.repository;

import io.management.ua.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByCategoryId(UUID categoryId);
    List<Category> getCategoriesByParentCategoryId(UUID parentCategoryId);
    @Query("SELECT c.name FROM Category c WHERE c.categoryId = :categoryId")
    String getCategoryNameByCategoryId(@Param("categoryId") UUID categoryId);
    @Modifying
    @Transactional
    @Query("UPDATE Category c SET c.enabled = :state WHERE c.categoryId IN :categoryIds")
    void switchStateOfCategoriesById(@Param("categoryIds") List<UUID> categoryIds, @Param("state") Boolean state);
}
