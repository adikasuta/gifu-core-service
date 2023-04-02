package com.gifu.coreservice.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
public class InvoiceDto {
    private String status;
    private String orderCode;
    private String productName;
    private String productTypeCode;
    private String notes;

    private String customerName;
    private String customerEmail;
    private String customerPhoneNo;

    private String provinceName;
    private String cityName;
    private String districtName;
    private String kelurahanName;
    private String postalCode;
    private String address;

    private Integer quantity;
    private BigDecimal productPrice;
    private BigDecimal variantPrice;
    private BigDecimal subTotal;
    private BigDecimal shippingFee;
    private BigDecimal chargeFee;
    private BigDecimal cashback;
    private BigDecimal discount;
    private BigDecimal grandTotal;

    private ZonedDateTime createdDate;
    private ZonedDateTime checkoutDate;

    private List<InvoiceVariantDto> variants;
}
