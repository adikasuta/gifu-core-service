package com.gifu.coreservice.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class InvoiceVariantDto {
    private String variantTypeCode;
    private String variantName;
    private String contentName;
    private List<KeyValueDto> additionalInfo;
}
