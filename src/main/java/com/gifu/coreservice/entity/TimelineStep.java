package com.gifu.coreservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "timeline_step")
@Data
public class TimelineStep {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "timeline_id")
    private Long timelineId;
    @Column(name = "assignee_user_id")
    private Long assigneeUserId;
    @Column(name = "step_id")
    private Long stepId;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;
    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;
}
