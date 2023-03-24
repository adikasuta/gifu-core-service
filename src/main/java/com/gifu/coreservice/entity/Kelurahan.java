package com.gifu.coreservice.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "villages")
@Data
public class Kelurahan {
    @Id
    private String id;

    private String name;
    @Column(name = "district_id")
    private String districtId;
}
