package com.gifu.coreservice.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "ref_status_pattern")
@Data
public class RefStatusPattern {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name;
    @Column(name = "pattern_code")
    private String patternCode;
}
