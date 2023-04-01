package com.gifu.coreservice.model.dto;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class OrderEventDetailDto {
    private long id;
    private Long orderId;
    private String name;
    private String venue;
    private ZonedDateTime date;
}
