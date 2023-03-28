package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.ProductVariantVisibilityRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductVariantVisibilityRuleRepository extends JpaRepository<ProductVariantVisibilityRule, Long> {
    List<ProductVariantVisibilityRule> findByProductVariantViewId(Long productVariantViewId);

    @Query("SELECT p FROM ProductVariantVisibilityRule p WHERE CONCAT(',', p.variantIds, ',') LIKE CONCAT('%,', :variantId, ',%')")
    List<ProductVariantVisibilityRule> findByVariantId(@Param("variantId") Long variantId);
}
