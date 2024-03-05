package io.management.ua.products.repository;

import io.management.ua.products.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProductId(UUID productId);
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.blocked = true, p.categoryId = null WHERE p.categoryId = :categoryId")
    void detachProductsFromCategory(@Param("categoryId") Long categoryId);
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.blocked = true WHERE p.vendorId = :vendorId")
    void blockVendorsProducts(@Param("vendorId") UUID vendorId);
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.blocked = false WHERE p.vendorId = :vendorId")
    void unblockVendorsProducts(@Param("vendorId") UUID vendorId);
}
