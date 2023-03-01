package com.gifu.coreservice.model.dto;

import lombok.Data;

@Data
public class OrderVariantDto {
    private Long variantId;
    private Long contentId;
    private Long pairVariantId;
    private Long pairContentId;
    private Integer quantity;
    private String contentText;
}
