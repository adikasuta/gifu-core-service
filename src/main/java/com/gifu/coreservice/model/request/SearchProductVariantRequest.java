package com.gifu.coreservice.model.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SearchProductVariantRequest {
    private String query;
    private String variantTypeCode;
}
