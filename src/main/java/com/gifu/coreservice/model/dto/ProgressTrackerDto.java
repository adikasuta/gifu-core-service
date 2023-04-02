package com.gifu.coreservice.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProgressTrackerDto {
    private String lastOrderStatus;
    private List<TimelineTrackerDto> timeline;
}
