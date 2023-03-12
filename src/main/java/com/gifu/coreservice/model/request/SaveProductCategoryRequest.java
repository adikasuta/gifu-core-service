package com.gifu.coreservice.model.request;

import com.gifu.coreservice.enumeration.ProductType;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class SaveProductCategoryRequest {
    private Long id;
    @NotEmpty
    private ProductType productType;
    @NotEmpty
    private String name;
    @NotNull
    private Integer designEstimation;
    @NotNull
    private Integer productionEstimation;
}
