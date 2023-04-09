package com.gifu.coreservice.model.request;

import com.gifu.coreservice.enumeration.PricingRangeFilter;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchProductRequest {
    private String searchQuery;
    private Long productCategoryId;
    private String productType;
    private PricingRangeFilter pricingRangeFilter;
    private Boolean available;
}
