package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.Content;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ContentRepository extends JpaRepository<Content, Long> {

    @Modifying
    void deleteByVariantId(Long variantId);
    List<Content> findByVariantId(Long variantId);
    Page<Content> findPageByVariantIdAndIsDeleted(Long variantId, boolean isDeleted, Pageable pageable);
}
