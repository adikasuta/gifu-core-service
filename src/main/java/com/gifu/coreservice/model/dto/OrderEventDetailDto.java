package com.gifu.coreservice.model.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;

@Data
public class OrderEventDetailDto {
    private long id;
    private Long orderId;
    private String name;
    private String venue;
    private LocalDate date;
    private LocalTime time;
}
