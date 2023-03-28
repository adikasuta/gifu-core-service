package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.ProductVariantView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductVariantViewRepository extends JpaRepository<ProductVariantView, Long> {
    List<ProductVariantView> findByProductId(Long productId);

    @Query("SELECT p FROM ProductVariantView p WHERE CONCAT(',', p.variantIds, ',') LIKE CONCAT('%,', :variantId, ',%')")
    List<ProductVariantView> findByVariantId(@Param("variantId") Long variantId);
}
