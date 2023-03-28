package com.gifu.coreservice.entity;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Set;

@Entity
@Table(name = "variant")
@Data
@SQLDelete(sql = "UPDATE variant SET is_deleted = true WHERE id=?")
public class Variant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "variant_code")
    private String variantCode;
    @Column(name = "variant_type_code")
    private String variantTypeCode;
    @Column(name = "name")
    private String name;
    @Column(name = "picture")
    private String picture;
    @Column(name = "allowed_be_secondary")
    private Boolean allowedBeSecondary;
    @Column(name = "updated_by")
    private String updatedBy;

    @Column(name = "is_deleted")
    private boolean isDeleted = Boolean.FALSE;

//    @OneToMany()
//    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
//    @JoinColumn(name = "variant_id")
//    private Set<Content> contents;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;
    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;
}
