package com.gifu.coreservice.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ProductVariantPriceDto {
    private List<Long> variantIds;
    private BigDecimal price;
}
