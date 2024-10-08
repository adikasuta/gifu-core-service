package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.OrderEventDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderEventDetailRepository extends JpaRepository<OrderEventDetail, Long> {
}
