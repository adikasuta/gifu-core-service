package com.gifu.coreservice.entity;

import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "provinces")
@Data
public class Province {
    @Id
    private String id;

    @Column(name = "name")
    private String name;
}
