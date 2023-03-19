package com.gifu.coreservice.model.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@Builder
public class DashboardOrderDto {
    private Long id;
    private String orderCode;
    private String productType;
    private String customerName;
    private String productName;
    private Integer quantity;
    private ZonedDateTime deadline;
    private BigDecimal grandTotal;
    private String status;

    private ZonedDateTime paymentDate;
    private ZonedDateTime eventDate;
}
