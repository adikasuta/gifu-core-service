package com.gifu.coreservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "timeline_step_status")
@Data
public class TimelineStepStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "step_timeline_detail_id")
    private Long stepTimelineDetailId;
    @Column(name = "status_id")
    private Long statusId;
    @Column(name = "created_by_user_id")
    private Long createdByUserId;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;
    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;
}
