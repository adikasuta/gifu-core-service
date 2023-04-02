package com.gifu.coreservice.model.dto;

import lombok.Data;

@Data
public class OrderVariantDto {
    private String variantTypeCode;
    private Long variantId;
    private Long contentId;
    private String additionalInfoValue;
    private String additionalInfoKey;
}
