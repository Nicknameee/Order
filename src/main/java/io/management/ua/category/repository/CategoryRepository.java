package io.management.ua.category.repository;

import io.management.ua.category.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByCategoryId(UUID categoryId);
    List<Category> getCategoriesByParentCategoryId(Long parentCategoryId);

    boolean existsByCategoryId(UUID categoryId);
}
