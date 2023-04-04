package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.Bill;
import com.gifu.coreservice.entity.Order;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BillRepository extends JpaRepository<Bill, Long>, JpaSpecificationExecutor<Bill> {
    List<Bill> findByOrderCheckoutPaymentIdAndStatusIn(Long orderCheckoutPaymentId, List<String> statuses);
    List<Bill> findByOrderCheckoutPaymentId(Long orderCheckoutPaymentId, Pageable pageable);
}
