package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.OrderShipping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderShippingRepository extends JpaRepository<OrderShipping, Long> {

}
