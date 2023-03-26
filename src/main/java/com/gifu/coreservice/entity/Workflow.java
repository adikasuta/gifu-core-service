package com.gifu.coreservice.entity;

import lombok.Data;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "workflow")
@Data
@SQLDelete(sql = "UPDATE workflow SET is_deleted = true WHERE id=?")
//@FilterDef(name = "deletedWorkflowFilter", parameters = @ParamDef(name = "isDeleted", type = "boolean"))
//@Filter(name = "deletedWorkflowFilter", condition = "is_deleted = :isDeleted")
public class Workflow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "workflowCode")
    private String workflowCode;
    @Column(name = "is_deleted")
    private boolean isDeleted = Boolean.FALSE;
    private String name;

    @OneToMany()
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinColumn(name = "workflow_id")
    private List<Step> steps;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "updated_by")
    private String updatedBy;
    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;
}
