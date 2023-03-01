package com.gifu.coreservice.model.request;

import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
public class ConfirmOrderRequest {
    private Long orderId;
    private BigDecimal shippingFee;
    private ZonedDateTime deadline;
    private String updaterEmail;
}
