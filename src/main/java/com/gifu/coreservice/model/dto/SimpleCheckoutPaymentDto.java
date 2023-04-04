package com.gifu.coreservice.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@Builder
public class SimpleCheckoutPaymentDto {
    private Long id;
    private Integer sequenceNo;
    private BigDecimal amount;
    private ZonedDateTime paymentDate;
    private boolean isPaid;
}
