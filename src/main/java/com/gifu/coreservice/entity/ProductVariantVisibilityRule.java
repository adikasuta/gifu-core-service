package com.gifu.coreservice.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "product_variant_visibility_rule")
@Data
public class ProductVariantVisibilityRule {
    //UI will show input form of product_variant_view_id
    //when there is another input with this variant_type_code, filled with one of these variant_ids
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "product_variant_view_id")
    private Long productVariantViewId;

    @Column(name = "variant_type_code")
    private String variantTypeCode;

    //split by comma
    //example: 1,2,3,4
    @Column(name = "variant_ids")
    private String variantIds;
}
