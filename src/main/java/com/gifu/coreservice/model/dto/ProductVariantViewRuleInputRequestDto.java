package com.gifu.coreservice.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProductVariantViewRuleInputRequestDto {
    private String variantTypeCode;
    private List<Long> variantIds;
}
