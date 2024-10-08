package com.gifu.coreservice.model.dto;

import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;

@Data
public class PricingRangeDto {
    private Integer qtyMin;
    private Integer qtyMax;
    private BigDecimal price;
}
