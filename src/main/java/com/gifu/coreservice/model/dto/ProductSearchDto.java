package com.gifu.coreservice.model.dto;

import com.gifu.coreservice.entity.PricingRange;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class ProductSearchDto {
    private Long id;
    private String name;
    private String picture;
    private String size;
    private Boolean available;
    private PricingRange displayPricing;
    private List<PricingRange> pricingRanges;
}
