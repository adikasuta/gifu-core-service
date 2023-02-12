package com.gifu.coreservice.model.request;

import com.gifu.coreservice.model.dto.StepDto;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class SaveWorkflowRequest {
    private Long workflowId;
    @NotEmpty
    private String workflowName;
    @NotEmpty
    private List<Long> categoryProductIds;
    @NotEmpty
    private List<StepDto> steps;
}
