package com.gifu.coreservice.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
public class CheckoutOrderDto {
    private Long orderCheckoutId;
    private String customerName;
    private String customerPhoneNo;
    private String customerEmail;
    private String paymentTerm;
    private ZonedDateTime createdDate;
    private BigDecimal grandTotalCheckout;
    private List<CheckoutOrderDetailDto> orders;
    private List<SimpleCheckoutPaymentDto> payments;
}
