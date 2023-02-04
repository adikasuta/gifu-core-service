package com.gifu.coreservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "status")
@Data
public class Status {
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
}
