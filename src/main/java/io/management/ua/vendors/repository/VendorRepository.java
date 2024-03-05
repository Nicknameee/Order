package io.management.ua.vendors.repository;

import io.management.ua.vendors.entity.Vendor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface VendorRepository extends JpaRepository<Vendor, UUID> {
    boolean existsByName(String name);
    boolean existsByEmail(String email);
    boolean existsByPhone(String phone);
    boolean existsByWebsite(String website);
}
