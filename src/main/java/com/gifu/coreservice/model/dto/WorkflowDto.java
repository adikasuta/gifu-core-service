package com.gifu.coreservice.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class WorkflowDto {
    private Long id;
    private String workflowCode;
    private String name;
    private List<ProductCategoryDto> productCategories;
    private List<ProductDto> products;
    private List<StepDto> steps;
}
