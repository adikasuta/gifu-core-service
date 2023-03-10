package com.gifu.coreservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "timeline")
@Data
public class Timeline {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "order_id")
    private Long orderId;
    @Column(name = "workflow_id")
    private Long workflowId;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;
    @Column(name = "created_by")
    private String createdBy;
}
