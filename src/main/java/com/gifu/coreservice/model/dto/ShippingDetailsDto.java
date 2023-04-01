package com.gifu.coreservice.model.dto;

import lombok.Data;

@Data
public class ShippingDetailsDto {
    private String provinceId;
    private String cityId;
    private String distrctId;
    private String kelurahanId;
    private String postalCode;
    private String address;
    private String preferredShippingVendor;
    private Boolean useWoodenCrate;
}
