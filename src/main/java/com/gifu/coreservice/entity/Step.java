package com.gifu.coreservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "step")
@Data
public class Step {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "workflow_id")
    private Long workflowId;
    private String name;
    @Column(name = "user_role_code")
    private String userRoleCode;
    @Column(name = "should_notify")
    private Boolean shouldNotify;
    @Column(name = "next_step_id")
    private Long nextStepId;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;
    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;
}
