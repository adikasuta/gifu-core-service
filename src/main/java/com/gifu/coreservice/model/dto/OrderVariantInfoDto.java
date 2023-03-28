package com.gifu.coreservice.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class OrderVariantInfoDto {
    private Long id;
    private String variantTypeCode;
//    private String variantCode;
//    private String variantContentCode;
    private String variantName;
    private String variantContentName;
    private String variantContentPicture;
    private List<KeyValueDto> additionalInfo;
}
