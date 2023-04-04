package com.gifu.coreservice.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
public class OrderDto {
    private Long id;
    private String customerName;
    private String customerEmail;
    private String customerPhoneNo;
    private String productName;
    private ZonedDateTime deadline;
    private Integer quantity;
    private String notes;
    private List<OrderVariantInfoDto> variantInfo;
}
