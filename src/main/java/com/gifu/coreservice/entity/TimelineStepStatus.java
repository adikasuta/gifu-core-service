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

    @Column(name = "timeline_step_id")
    private Long timelineStepId;
    @Column(name = "status_id")
    private Long statusId;
    @Column(name = "status_name")
    private String statusName;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;
    @Column(name = "created_by")
    private String createdBy;
}
