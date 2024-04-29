package io.management.ua.products.repository;

import io.management.ua.products.entity.WaitingListProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface WaitingListProductRepository extends JpaRepository<WaitingListProduct, Long> {
    @Query("SELECT w.productId FROM WaitingListProduct w WHERE w.customerId = :customerId")
    List<UUID> getProductIdsByCustomerId(@Param("customerId") Long customerId);
    boolean existsByCustomerIdAndProductId(Long customerId, UUID productId);
    @Modifying
    @Transactional
    @Query("DELETE FROM WaitingListProduct w WHERE w.customerId = :customerId AND w.productId = :productId")
    int removeWaitingListEntry(@Param("customerId") Long customerId, @Param("productId") UUID productId);
}
