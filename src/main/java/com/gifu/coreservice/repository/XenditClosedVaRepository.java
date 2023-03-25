package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.XenditClosedVa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface XenditClosedVaRepository extends JpaRepository<XenditClosedVa, Long> {

    List<XenditClosedVa> findByVaNumberAndStatusIn(String vaNumber, List<String> statuses);
    Optional<XenditClosedVa> findByXenditId(String xenditId);
    List<XenditClosedVa> findByBillId(Long billId);
}
