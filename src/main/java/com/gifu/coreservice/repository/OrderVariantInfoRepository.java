package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.OrderVariantInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderVariantInfoRepository extends JpaRepository<OrderVariantInfo, Long> {
    List<OrderVariantInfo> findByOrderVariantId(Long orderVariantId);
}
