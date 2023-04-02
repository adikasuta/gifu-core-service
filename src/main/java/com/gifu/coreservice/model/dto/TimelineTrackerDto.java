package com.gifu.coreservice.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.ZonedDateTime;

@Data
@Builder
public class TimelineTrackerDto {
    private String stepName;
    private String lastStatus;
    private ZonedDateTime createdDate;
}
