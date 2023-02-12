package com.gifu.coreservice.entity;

import lombok.Data;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.List;

@Entity
@Table(name = "workflow")
@Data
public class Workflow {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "workflowCode")
    private String workflowCode;
    @Column(name = "is_deleted")
    private Boolean isDeleted;
    private String name;

    @OneToMany()
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinColumn(name = "workflow_id")
    private List<Step> steps;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;
    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;
}
