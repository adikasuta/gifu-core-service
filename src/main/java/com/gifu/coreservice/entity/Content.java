package com.gifu.coreservice.entity;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Set;

@Entity
@Table(name = "content")
@Data
public class Content {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "variant_code")
    private String variantCode;
    @Column(name = "name")
    private String name;
    @Column(name = "picture")
    private String picture;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;
    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;
}
