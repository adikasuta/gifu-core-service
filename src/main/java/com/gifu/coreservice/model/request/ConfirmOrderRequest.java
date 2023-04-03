package com.gifu.coreservice.model.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZonedDateTime;

@Data
public class ConfirmOrderRequest {
    private Long orderId;
    private BigDecimal shippingFee;
    private LocalDate deadline;
    private String updaterEmail;
}
