package com.gifu.coreservice.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "regencies")
@Data
public class City {
    @Id
    private String id;

    private String name;

    @Column(name = "province_id")
    private String provinceId;
}
