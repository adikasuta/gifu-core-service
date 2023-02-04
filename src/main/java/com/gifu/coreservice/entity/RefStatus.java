package com.gifu.coreservice.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "ref_status")
@Data
public class RefStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    @Column(name = "status_pattern_code")
    private String statusPatternCode;
    @Column(name = "next_status_id")
    private Long nextStatusId;
}
