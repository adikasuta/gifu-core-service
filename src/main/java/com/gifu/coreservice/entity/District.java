package com.gifu.coreservice.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "districts")
@Data
public class District {
    @Id
    private String id;

    private String name;
    @Column(name = "regency_id")
    private String cityId;
}
