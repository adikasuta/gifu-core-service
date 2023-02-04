package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.TimelineStepStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimelineStepStatusRepository extends JpaRepository<TimelineStepStatus, Long> {
}
