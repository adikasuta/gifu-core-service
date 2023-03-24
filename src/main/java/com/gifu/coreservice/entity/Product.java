package com.gifu.coreservice.entity;

import lombok.Data;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.FilterDef;
import org.hibernate.annotations.ParamDef;
import org.hibernate.annotations.SQLDelete;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Set;

@Entity
@Table(name = "product")
@Data
@SQLDelete(sql = "UPDATE product SET is_deleted = true WHERE id=?")
@FilterDef(name = "deletedProductFilter", parameters = @ParamDef(name = "isDeleted", type = "boolean"))
@Filter(name = "deletedProductFilter", condition = "is_deleted = :isDeleted")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "product_code")
    private String productCode;
    @Column(name = "product_category_id")
    private Long productCategoryId;
    @Column(name = "product_type")
    private String productType;
    @Column(name = "name")
    private String name;
    @Column(name = "picture")
    private String picture;
    @Column(name = "price")
    private BigDecimal price;
    @Column(name = "length")
    private Double length;
    @Column(name = "width")
    private Double width;
    @Column(name = "height")
    private Double height;
    @Column(name = "weight")
    private Double weight;
    @Column(name = "min_order")
    private Integer minOrder;
    @Column(name = "description")
    private String description;
    @Column(name = "is_deleted")
    private boolean isDeleted = Boolean.FALSE;
    @Column(name = "is_not_available")
    private Boolean isNotAvailable;

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Set<PricingRange> pricingRanges;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;
    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;
    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "updated_by")
    private String updatedBy;
}
