package com.gifu.coreservice.model.dto;

import com.gifu.coreservice.enumeration.VariantTypeEnum;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProductVariantViewInputRequestDto {
    private VariantTypeEnum variantTypeCode;
    private List<Long> variantIds;
    private List<ProductVariantViewRuleInputRequestDto> rules;
}
