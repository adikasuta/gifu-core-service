package com.gifu.coreservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "cs_referral")
@Data
public class CsReferral {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String token;
    @Column(name = "user_id")
    private Long userId;
    @Column(name = "created_date")
    private ZonedDateTime createdDate;
    @Column(name = "inactive_date")
    private ZonedDateTime inactiveDate;
}
