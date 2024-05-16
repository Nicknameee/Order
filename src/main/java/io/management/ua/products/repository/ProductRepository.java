package io.management.ua.products.repository;

import io.management.ua.products.dto.WaitingListProductDTO;
import io.management.ua.products.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByProductId(UUID productId);
    @Modifying
    @Transactional
    @Query("UPDATE Product p SET p.blocked = true, p.categoryId = null WHERE p.categoryId = :categoryId")
    void detachProductsFromCategory(@Param("categoryId") UUID categoryId);
    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE CONCAT('%', :searchBy, '%') ORDER BY " +
            "CASE WHEN p.name LIKE CONCAT(:searchBy, '%') THEN 1 WHEN p.name LIKE CONCAT('%', :searchBy, '%') THEN 2 ELSE 3 END")
    Page<Product> getProductsByNamePartial(@Param("searchBy") String searchBy, Pageable of);
    @Query("SELECT p.itemsLeft FROM Product p WHERE p.productId = :productId")
    Long getProductAmount(@Param("productId") UUID productId);
    boolean existsByProductId(UUID productId);
    @Query("SELECT NEW io.management.ua.products.dto.WaitingListProductDTO(p.id, p.name, p.productId, p.cost, p.currency, p.categoryId, p.introductionPictureUrl) FROM Product p WHERE p.productId IN (:productIds)")
    List<WaitingListProductDTO> getWaitingListForCustomer(@Param("productIds") List<UUID> productIds);
}
