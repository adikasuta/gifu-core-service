package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.PaymentResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PaymentResponseRepository extends JpaRepository<PaymentResponse, String> {
}
