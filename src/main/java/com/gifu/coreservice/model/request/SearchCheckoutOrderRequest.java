package com.gifu.coreservice.model.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SearchCheckoutOrderRequest {
    private Long productCategoryId;
    private String productType;
    private LocalDate periodFrom;
    private LocalDate periodUntil;
    private String query;
}
