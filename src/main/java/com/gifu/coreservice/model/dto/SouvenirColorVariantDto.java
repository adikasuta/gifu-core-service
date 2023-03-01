package com.gifu.coreservice.model.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class SouvenirColorVariantDto {
    private String productName;
    private String colorName;
    private String packagingName;
    private Integer quantity;
    private BigDecimal price;
}
