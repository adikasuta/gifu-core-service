package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.HistoricalOrderStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HistoricalOrderStatusRepository extends JpaRepository<HistoricalOrderStatus, Long> {

    List<HistoricalOrderStatus> findByOrderId(Long orderId, Pageable pageable);
}
