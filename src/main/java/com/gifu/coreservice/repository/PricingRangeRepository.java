package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.PricingRange;
import com.gifu.coreservice.entity.ProductCategory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PricingRangeRepository extends JpaRepository<PricingRange, Long>, JpaSpecificationExecutor<PricingRange> {
    List<PricingRange> findByProductId(Long productId);

    @Query("FROM PricingRange WHERE productId = :productId AND qtyMax IS NULL")
    List<PricingRange> findByProductIdAndQtyMaxIsNull(@Param("productId") Long productId);

    @Query("FROM PricingRange where productId = :productId")
    List<PricingRange> findByProductIdWithPageable(Long productId, Pageable pageable);
}
