package com.gifu.coreservice.model.request;

import lombok.Data;

@Data
public class UpdateStepStatusRequest {
    private Long timelineId;
    private Long currentStepId;
    private Long newStatusId;
}
