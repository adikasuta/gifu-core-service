package com.gifu.coreservice.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class CheckoutOrderDto {
    private Long orderCheckoutId;
    private String customerName;
    private String customerPhoneNo;
    private String customerEmail;
    private String paymentTerm;
    private BigDecimal grandTotalCheckout;
    private List<CheckoutOrderDetailDto> orders;
}
