package com.gifu.coreservice.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Set;

@Entity
@Table(name = "user")
@Data
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "employee_code")
    private String employeeCode;
    private String name;
    private String email;
    private String password;
    @Column(name = "inactive_date")
    private ZonedDateTime inactiveDate;
    @Column(name = "role_id")
    private Long roleId;
    @Column(name = "created_date")
    private ZonedDateTime createdDate;
    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;

    @ManyToOne
    private CsReferral csReferral;

    @OneToMany()
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinColumn(name = "product_code")
    private Set<Permission> permissions;
}
