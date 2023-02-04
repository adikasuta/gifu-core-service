package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.Variant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VariantRepository extends JpaRepository<Variant, Long> {
}
