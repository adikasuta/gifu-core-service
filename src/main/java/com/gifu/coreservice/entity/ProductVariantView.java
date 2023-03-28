package com.gifu.coreservice.entity;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "product_variant_view")
@Data
public class ProductVariantView {

    // order page will list variant_type_code selection
    // according to variant_ids
    // the input ui will only show when product_variant_visibility_rule is fulfilled
    // unless the admin didn't put any setting.
    // please check ProductVariantVisibilityRule.class to see the rule

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "product_id")
    private Long productId;

    @Column(name = "variant_type_code")
    private String variantTypeCode;

    //split by comma
    //example: 1,2,3,4
    @Column(name = "variant_ids")
    private String variantIds;
}
