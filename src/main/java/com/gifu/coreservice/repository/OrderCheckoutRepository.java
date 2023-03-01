package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.OrderCheckout;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderCheckoutRepository extends JpaRepository<OrderCheckout, Long> {
}
