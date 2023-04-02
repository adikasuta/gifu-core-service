package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.OrderVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderVariantRepository extends JpaRepository<OrderVariant, Long> {
    List<OrderVariant> findByOrderId(Long orderId);
    List<OrderVariant> findByOrderIdAndVariantId(Long orderId, Long variantId);
}
