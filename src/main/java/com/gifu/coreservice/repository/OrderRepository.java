package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.Order;
import com.gifu.coreservice.entity.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    List<Order> findByOrderCheckoutId(Long orderCheckoutId);
    long countByOrderCode(String orderCode);
}
