package com.gifu.coreservice.model.dto;

import com.gifu.coreservice.entity.OrderShipping;
import com.gifu.coreservice.enumeration.ShippingVendor;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@Builder
public class DashboardOrderDto {
    private Long id;
    private ZonedDateTime createdDate;
    private ZonedDateTime checkoutDate;
    private String orderCode;
    private String productType;
    private String customerName;
    private String productName;
    private Integer quantity;
    private ZonedDateTime deadline;
    private BigDecimal grandTotal;
    private String status;
    private String statusText;

    private String provinceName;
    private String cityName;
    private String districtName;
    private String kelurahanName;
    private String postalCode;
    private String address;

    private String shippingVendorName;
    private String shippingVendorCode;

    private ZonedDateTime paymentDate;
}
