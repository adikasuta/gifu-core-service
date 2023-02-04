package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.HistoricalOrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface HistoricalOrderStatusRepository extends JpaRepository<HistoricalOrderStatus, Long> {
}
