package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.OrderVariantInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderVariantInfoRepository extends JpaRepository<OrderVariantInfo, Long> {
}
