package com.gifu.coreservice.model.request;

import com.gifu.coreservice.enumeration.ProductType;
import com.gifu.coreservice.model.dto.PricingRangeDto;
import com.gifu.coreservice.model.dto.ProductVariantDto;
import lombok.Data;

import java.util.List;

@Data
public class SaveProductRequest {
    private Long id;
    private ProductType productType;
    private String existingPicture;
    private String name;
    private Long categoryId;
    private Double width;
    private Double height;
    private Double length;
    private Double weight;
    private Integer minOrder;
    private String description;

    private List<ProductVariantDto> productVariants;
    private List<PricingRangeDto> pricingRanges;
}
