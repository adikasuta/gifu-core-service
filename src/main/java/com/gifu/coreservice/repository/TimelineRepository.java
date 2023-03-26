package com.gifu.coreservice.repository;

import com.gifu.coreservice.entity.Timeline;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TimelineRepository extends JpaRepository<Timeline, Long> {
    List<Timeline> findByOrderId(Long orderId);

    @Query("FROM Timeline WHERE isDone = false")
    List<Timeline> findAllActiveTimeline();
}
