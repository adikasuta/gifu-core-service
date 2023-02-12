package com.gifu.coreservice.entity;

import lombok.Data;
import org.hibernate.annotations.Cache;
import org.hibernate.annotations.CacheConcurrencyStrategy;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Set;

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
    @Column(name = "greetings_varian_id")
    private Long greetingsVarianId;
    @Column(name = "price")
    private BigDecimal price;
}
