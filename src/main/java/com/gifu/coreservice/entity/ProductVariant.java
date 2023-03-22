package com.gifu.coreservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "product_variant")
@Data
public class ProductVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "product_id")
    private Long productId;
    @Column(name = "variant_id")
    private Long variantId;
    @Column(name = "pair_variant_id")
    private Long pairVariantId;
    @Column(name = "greetings_variant_id")
    private Long greetingsVariantId;
    @Column(name = "price")
    private BigDecimal price;
}
