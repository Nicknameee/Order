package io.management.ua.products.repository;

import io.lettuce.core.dynamic.annotation.Param;
import io.management.ua.products.entity.ProductModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface ProductRepository extends JpaRepository<ProductModel, Long> {
    @Modifying
    @Transactional
    @Query("UPDATE ProductModel p SET p.blocked = true WHERE p.productId = :productId")
    void disableProduct(@Param("productId") Long productId);
}
