package com.gifu.coreservice.entity;

import lombok.Data;
import org.hibernate.annotations.*;
import org.hibernate.annotations.Cache;

import javax.persistence.*;
import javax.persistence.Entity;
import javax.persistence.Table;
import java.time.ZonedDateTime;
import java.util.Set;

@Entity
@Table(name = "product_category")
@Data
@SQLDelete(sql = "UPDATE product_category SET is_deleted = true WHERE id=?")
@FilterDef(name = "deletedCategoryFilter", parameters = @ParamDef(name = "isDeleted", type = "boolean"))
@Filter(name = "deletedCategoryFilter", condition = "is_deleted = :isDeleted")
public class ProductCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "name")
    private String name;
    @Column(name = "picture")
    private String picture;
    @Column(name = "workflow_code")
    private String workflowCode;
    @Column(name = "product_type")
    private String productType;
    @Column(name = "design_estimation")
    private Integer designEstimation;
    @Column(name = "production_estimation")
    private Integer productionEstimation;
    @Column(name = "is_deleted")
    private boolean isDeleted = Boolean.FALSE;

    @OneToMany(fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinColumn(name = "product_category_id")
    private Set<Product> products;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;
    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;
}
