package com.gifu.coreservice.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
public class PaymentInstructionDto {
    private Long orderCheckoutPaymentId;
    private Long billId;
    private BigDecimal amount;
    private ZonedDateTime paymentDate;
    private ZonedDateTime expiryDate;
    private List<PaymentInstructionVADto> virtualAccounts;
}
