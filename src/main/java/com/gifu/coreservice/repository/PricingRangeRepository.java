package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.PricingRange;
import com.gifu.coreservice.entity.ProductVariant;
import com.gifu.coreservice.model.dto.PricingRangeDto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PricingRangeRepository extends JpaRepository<PricingRange, Long> {
    List<PricingRange> findByProductId(Long productId);

    @Query("FROM PricingRange where productId = :productId and qtyMax IS NULL LIMIT 1")
    Optional<PricingRange> findByProductIdAndQtyMaxIsNull(Long productId);

    @Query("FROM PricingRange where productId = :productId ORDER BY qtyMax DESC LIMIT 1")
    Optional<PricingRange> findByProductIdAndHighestQty(Long productId);
}
