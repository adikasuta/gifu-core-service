package com.gifu.coreservice.entity;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.time.ZonedDateTime;
import java.util.Set;

@Entity
@Table(name = "product_category")
@Data
public class ProductCategory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "product_category_code")
    private String productCategoryCode;
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

    @OneToMany(fetch = FetchType.EAGER)
    @Cache(usage = CacheConcurrencyStrategy.NONSTRICT_READ_WRITE)
    @JoinColumn(name = "product_category_id")
    private Set<Product> products;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;
    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;
}
