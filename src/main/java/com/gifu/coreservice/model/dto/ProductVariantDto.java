package com.gifu.coreservice.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class ProductVariantDto {
    private Long mainVariantId;
    private Long firstSubVariantId;
    private Long secondSubVariantId;
    private BigDecimal price;
}
