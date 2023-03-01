package com.gifu.coreservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "order_variant")
@Data
public class OrderVariant {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "order_id")
    private Long orderId;
    @Column(name = "variant_type_code")
    private String variantTypeCode;
    @Column(name = "variant_code")
    private String variantCode;
    @Column(name = "variant_name")
    private String variantName;
    @Column(name = "variant_content_code")
    private String variantContentCode;
    @Column(name = "variant_content_name")
    private String variantContentName;
    @Column(name = "variant_content_picture")
    private String variantContentPicture;

    @Column(name = "order_variant_price_id")
    private Long orderVariantPriceId;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;
    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;
}
