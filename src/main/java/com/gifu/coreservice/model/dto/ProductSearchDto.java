package com.gifu.coreservice.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class ProductSearchDto {
    private Long id;
    private String name;
    private String picture;
    private BigDecimal price;
    private String size;
}
