package com.gifu.coreservice.model.dto;

import lombok.Data;

@Data
public class OrderVariantDto {
    private Long variantId;
    private Long contentId;
    private Integer quantity;
    private String contentText;
}
