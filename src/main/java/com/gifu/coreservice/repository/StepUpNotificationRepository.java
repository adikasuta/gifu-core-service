package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.StepUpNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StepUpNotificationRepository extends JpaRepository<StepUpNotification, String> {

    List<StepUpNotification> findByIdBillingAndAcknowledgedIsNull(String idBilling);
}
