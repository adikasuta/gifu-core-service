package com.gifu.coreservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "user_permission")
@Data
public class UserPermission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "user_id")
    private Long userId;
    @Column(name = "permission_id")
    private Long permissionId;
    @Column(name = "is_include")
    private boolean isInclude;
    @Column(name = "created_date")
    private ZonedDateTime createdDate;
    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;
}
