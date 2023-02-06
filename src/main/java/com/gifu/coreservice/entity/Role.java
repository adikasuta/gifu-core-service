package com.gifu.coreservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Set;

@Entity
@Table(name = "role")
@Data
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String name;
    private String code;
    @Column(name = "created_date")
    private ZonedDateTime createdDate;
    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;

    @ManyToMany(cascade = {CascadeType.ALL})
    @JoinTable(
            name = "role_permission",
            inverseJoinColumns = {@JoinColumn(name = "permission_id")},
            joinColumns = {@JoinColumn(name = "role_id")}
    )
    private Set<Permission> permissions;
}
