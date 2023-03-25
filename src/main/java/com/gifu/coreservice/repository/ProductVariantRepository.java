package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.ProductVariant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    List<ProductVariant> findByProductId(Long productId);
    @Query("SELECT COUNT(a) FROM ProductVariant a WHERE variantId = :variantId OR firstSubvariantId = :variantId OR secondSubvariantId = :variantId")
    long countByVariations(Long variantId);
    @Query("FROM ProductVariant WHERE variantId = :variantId OR firstSubvariantId = :variantId OR secondSubvariantId = :variantId")
    List<ProductVariant> findByVariations(Long variantId);

}
