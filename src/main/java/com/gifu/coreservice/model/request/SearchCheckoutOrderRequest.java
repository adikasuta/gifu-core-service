package com.gifu.coreservice.model.request;

import lombok.Data;

@Data
public class SearchCheckoutOrderRequest {
    private String querySearch;
    private String productTypeCode;
    private Long productCategoryId;
}
