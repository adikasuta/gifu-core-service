package com.gifu.coreservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Set;

@Entity
@Table(name = "product")
@Data
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "product_code")
    private String productCode;
    @Column(name = "product_category_id")
    private Long productCategoryId;
    @Column(name = "product_type")
    private Long productType;
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

    @OneToMany(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id")
    private Set<PricingRange> pricingRanges;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;
    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;
}
