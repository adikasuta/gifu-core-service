package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.OrderAdditionalInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderAdditionalInfoRepository extends JpaRepository<OrderAdditionalInfo, Long> {
}
