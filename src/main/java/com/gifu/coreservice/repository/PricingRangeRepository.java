package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.PricingRange;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PricingRangeRepository extends JpaRepository<PricingRange, Long> {
}
