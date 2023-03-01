package com.gifu.coreservice.model.dto;

import lombok.Data;

@Data
public class ShippingDetailsDto {
    private Long provinceId;
    private Long cityId;
    private Long distrctId;
    private Long kelurahanId;
    private String postalCode;
    private String address;
    private Long preferredShippingVendor;
    private Boolean useWoodenCrate;
}
