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
    @Query("SELECT COUNT(a) FROM ProductVariant a WHERE variantId = :variantId OR pairVariantId = :variantId OR greetingsVariantId = :variantId")
    long countByVariations(Long variantId);
    @Query("FROM ProductVariant WHERE variantId = :variantId OR pairVariantId = :variantId OR greetingsVariantId = :variantId")
    List<ProductVariant> findByVariations(Long variantId);
    Optional<ProductVariant> findByProductIdAndVariantId(Long productId, Long variantId);
    Optional<ProductVariant> findByProductIdAndVariantIdAndPairVariantId(Long productId, Long variantId, Long pairVariantId);

}
