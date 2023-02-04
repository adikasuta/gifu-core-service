package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.TimelineStep;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TimelineStepRepository extends JpaRepository<TimelineStep, Long> {
}
