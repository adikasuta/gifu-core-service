package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.XenditVaPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface XenditVaPaymentRepository extends JpaRepository<XenditVaPayment, Long> {
    Optional<XenditVaPayment> findByXenditId(String xenditId);
}
