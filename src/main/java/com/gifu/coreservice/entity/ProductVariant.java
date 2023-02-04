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

    @Column(name = "product_code")
    private String productCode;
    @Column(name = "variant_code")
    private String variantCode;
    @Column(name = "pair_variant_code")
    private String pairVariantCode;
    @Column(name = "greetings_varian_code")
    private String greetingsVarianCode;
    @Column(name = "price")
    private BigDecimal price;
}
