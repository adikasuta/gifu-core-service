package com.gifu.coreservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "order_shipping")
@Data
public class OrderShipping {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "order_id")
    private Long orderId;
    @Column(name = "province_code")
    private String provinceCode;
    @Column(name = "city_code")
    private String cityCode;
    @Column(name = "district_code")
    private String districtCode;
    @Column(name = "kelurahan_code")
    private String kelurahanCode;
    @Column(name = "address")
    private String address;
    @Column(name = "preferred_shipping_vendor")
    private Long preferredShippingVendor;
    @Column(name = "use_wooden_crate")
    private Boolean useWoodenCrate;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;
    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;
}
