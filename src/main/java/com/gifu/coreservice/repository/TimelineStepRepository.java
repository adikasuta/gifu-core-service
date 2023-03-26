package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.TimelineStep;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimelineStepRepository extends JpaRepository<TimelineStep, Long> {
    @Query("FROM TimelineStep WHERE timelineId = :timelineId")
    List<TimelineStep> findTimelineStepByTimelineIdWithPageable(Long timelineId, Pageable pageable);
}
