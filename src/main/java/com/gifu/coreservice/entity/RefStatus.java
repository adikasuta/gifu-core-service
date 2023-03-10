package com.gifu.coreservice.entity;

import com.gifu.coreservice.model.util.Flow;
import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "ref_status")
@Data
public class RefStatus implements Flow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    @Column(name = "pattern_code_id")
    private Long patternCodeId;

    @Column(name = "permission_code")
    private String permissionCode;
    @Column(name = "next_status_id")
    private Long nextStatusId;
    @Column(name = "created_date")
    private ZonedDateTime createdDate;
    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;

    @Override
    public Long getCurrentStepId() {
        return id;
    }

    @Override
    public Long getNextStepId() {
        return nextStatusId;
    }
}
