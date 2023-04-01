package com.gifu.coreservice.model.dto.order.product;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContentReferenceDto {
    private Long id;
    private Long variantId;
    private String name;
    private String picture;
}
