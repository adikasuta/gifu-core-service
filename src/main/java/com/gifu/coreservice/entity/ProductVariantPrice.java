package com.gifu.coreservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "product_variant_price")
@Data
public class ProductVariantPrice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name="variant_ids")
    private String variantIds;
    @Column(name = "product_id")
    private Long productId;
    @Column(name = "price")
    private BigDecimal price;
}
