package com.gifu.coreservice.model.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchProductRequest {
    private String searchQuery;
    private Long productCategoryId;
    private Long productType;
}
