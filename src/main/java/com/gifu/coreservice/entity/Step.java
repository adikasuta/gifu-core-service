package com.gifu.coreservice.entity;

import com.gifu.coreservice.model.util.Flow;
import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Set;

@Entity
@Table(name = "step")
@Data
public class Step implements Flow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "workflow_id")
    private Long workflowId;
    private String name;
    @Column(name = "user_role_code")
    private String userRoleCode;
    @Column(name = "default_assigned_user_id")
    private Long defaultAssignedUserId;
    @Column(name = "should_notify")
    private Boolean shouldNotify;
    @Column(name = "next_step_id")
    private Long nextStepId;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "step_id")
    private Set<Status> statuses;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;
    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;

    @Override
    public Long getCurrentStepId() {
        return id;
    }
}
