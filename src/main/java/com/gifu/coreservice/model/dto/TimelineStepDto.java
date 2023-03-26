package com.gifu.coreservice.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class TimelineStepDto {
    private Long id;
    private String stepName;
    private ZonedDateTime createdDate;
    private String createdBy;
}
