package com.gifu.coreservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "pricing_range")
@Data
public class PricingRange {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "product_code")
    private String productCode;
    @Column(name = "qty_min")
    private Integer qtyMin;
    @Column(name = "qty_max")
    private Integer qtyMax;
    @Column(name = "price")
    private BigDecimal price;
}
