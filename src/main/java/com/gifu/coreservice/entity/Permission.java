package com.gifu.coreservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Set;

@Entity
@Table(name = "permission")
@Data
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String code;
    private String name;
    private String remarks;
    @Column(name = "created_date")
    private ZonedDateTime createdDate;
    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "user_permission",
            joinColumns = {@JoinColumn(name = "permission_id")},
            inverseJoinColumns = {@JoinColumn(name = "user_id")}
    )
    private Set<User> users;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "role_permission",
            joinColumns = {@JoinColumn(name = "permission_id")},
            inverseJoinColumns = {@JoinColumn(name = "role_id")}
    )
    private Set<Role> roles;
}
