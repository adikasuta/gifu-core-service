package com.gifu.coreservice.model.dto;

import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;

@Data
@Builder
public class ProductCategoryDto {
    private Long id;
    private String name;
    private String picture;
    private String workflowCode;
    private String productType;
    private Integer designEstimation;
    private Integer productionEstimation;
}
