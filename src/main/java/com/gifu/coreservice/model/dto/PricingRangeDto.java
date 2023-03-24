package com.gifu.coreservice.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class PricingRangeDto {
    private Integer minQuantity;
    private Integer maxQuantity;
    private BigDecimal price;
}
