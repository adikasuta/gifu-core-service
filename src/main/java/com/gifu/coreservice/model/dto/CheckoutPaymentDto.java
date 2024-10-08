package com.gifu.coreservice.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
public class CheckoutPaymentDto {
    private Long orderCheckoutPaymentId;
    private Integer sequenceNo;
    private BigDecimal amount;
    private Long billId;
    private ZonedDateTime paymentDate;
    private ZonedDateTime expiryDate;
    private List<PaymentInstructionVADto> virtualAccounts;
}
