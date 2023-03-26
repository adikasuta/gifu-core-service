package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.TimelineStepStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimelineStepStatusRepository extends JpaRepository<TimelineStepStatus, Long> {
    @Query("FROM TimelineStepStatus WHERE timelineStepId = :timelineStepId")
    List<TimelineStepStatus> findByTimelineStepIdWithPageable(Long timelineStepId, Pageable pageable);
}
