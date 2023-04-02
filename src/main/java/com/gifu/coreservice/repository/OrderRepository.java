package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.Order;
import com.gifu.coreservice.entity.Workflow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long>, JpaSpecificationExecutor<Order> {
    List<Order> findByOrderCheckoutId(Long orderCheckoutId);
    long countByOrderCode(String orderCode);
    Optional<Order> findByOrderCode(String orderCode);

    List<Order> findByCustomerEmailAndStatus(String customerEmail, String status);
}
