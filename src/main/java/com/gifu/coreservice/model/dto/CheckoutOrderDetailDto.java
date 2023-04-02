package com.gifu.coreservice.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CheckoutOrderDetailDto {
    private Long orderId;
    private String orderCode;
    private String orderStatus;
    private String productName;
    private BigDecimal grandTotal;
}
