package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.OrderCheckoutPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderCheckoutPaymentRepository extends JpaRepository<OrderCheckoutPayment, Long> {
    Optional<OrderCheckoutPayment> findByOrderCheckoutIdAndSequenceNo(Long orderCheckoutId, Integer sequenceNo);

    List<OrderCheckoutPayment> findByOrderCheckoutId(Long orderCheckoutId);
}
