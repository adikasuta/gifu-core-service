package com.gifu.coreservice.model.dto.dashboard.product;

import com.gifu.coreservice.entity.ProductVariantVisibilityRule;
import com.gifu.coreservice.enumeration.VariantTypeEnum;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProductVariantViewDto {
    private Long id;
    private VariantTypeEnum variantTypeCode;
    private String variantIds;
    private List<ProductVariantVisibilityRule> rules;
}
