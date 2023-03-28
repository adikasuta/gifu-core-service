package com.gifu.coreservice.model.dto.order;

import com.gifu.coreservice.entity.PricingRange;
import com.gifu.coreservice.entity.ProductVariantPrice;
import com.gifu.coreservice.model.dto.ProductVariantViewInputRequestDto;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProductOrderDto {
    private Long id;
    private String productCode;
    private Long productCategoryId;
    private String productType;
    private String name;
    private String existingPicture;
    private Double length;
    private Double width;
    private Double height;
    private Double weight;
    private Integer minOrder;
    private String description;

    private List<PricingRange> pricingRanges;
    private List<ProductVariantPrice> productVariantPrices;
    private List<ProductVariantViewDto> productVariantViews;
}
