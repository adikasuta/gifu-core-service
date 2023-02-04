package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.ShippingVendor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShippingVendorRepository extends JpaRepository<ShippingVendor, Long> {
}
