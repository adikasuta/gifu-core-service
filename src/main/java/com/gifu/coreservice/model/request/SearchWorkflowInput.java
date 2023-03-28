package com.gifu.coreservice.model.request;

import lombok.Data;

@Data
public class SearchWorkflowInput {
    private Long productCategoryId;
    private String query;
}
