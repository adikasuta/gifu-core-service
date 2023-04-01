package com.gifu.coreservice.model.dto.order.product;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class VariantReferenceDto {
    private Long id;
    private String variantCode;
    private String variantTypeCode;
    private String name;
    private String picture;
    private List<ContentReferenceDto> contents;
}
