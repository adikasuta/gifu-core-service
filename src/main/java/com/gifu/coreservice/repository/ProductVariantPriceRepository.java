package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.ProductVariantPrice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductVariantPriceRepository extends JpaRepository<ProductVariantPrice, Long> {

    List<ProductVariantPrice> findByProductId(Long productId);

    @Query("SELECT COUNT(p) FROM ProductVariantPrice p WHERE CONCAT(',', p.variantIds, ',') LIKE CONCAT('%,', :variantId, ',%')")
    long countByVariantId(Long variantId);

    @Query("SELECT p FROM ProductVariantPrice p WHERE CONCAT(',', p.variantIds, ',') LIKE CONCAT('%,', :variantId, ',%')")
    List<ProductVariantPrice> findByVariantId(@Param("variantId") Long variantId);

}
