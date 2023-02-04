package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.OrderBrideGroom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderBrideGroomRepository extends JpaRepository<OrderBrideGroom, Long> {
}
