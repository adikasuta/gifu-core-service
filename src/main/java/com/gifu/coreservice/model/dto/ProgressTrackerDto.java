package com.gifu.coreservice.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class ProgressTrackerDto {
    private String orderCode;
    private String productName;
    private Integer quantity;
    private String remarks;
    private String lastOrderStatus;
    private List<TimelineTrackerDto> timeline;
}
