package com.gifu.coreservice.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class CartItemDto {
    private String orderCode;
    private String productName;
    private String productType;
    private Integer quantity;
    private String productImage;
    private BigDecimal productPrice;
    private BigDecimal variantPrice;
    private BigDecimal subTotal;
    private BigDecimal shippingFee;
    private BigDecimal chargeFee;
    private BigDecimal cashback;
    private BigDecimal discount;
    private BigDecimal grandTotal;
}
