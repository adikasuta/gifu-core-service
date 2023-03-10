package com.gifu.coreservice.entity;

import com.gifu.coreservice.model.util.Flow;
import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "status")
@Data
public class Status implements Flow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;

    @Column(name = "step_id")
    private Long stepId;
    @Column(name = "permission_code")
    private String permissionCode;
    @Column(name = "next_status_id")
    private Long nextStatusId;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "updated_by")
    private String updatedBy;
    @Override
    public Long getCurrentStepId() {
        return id;
    }
    @Override
    public Long getNextStepId() {
        return nextStatusId;
    }
}
