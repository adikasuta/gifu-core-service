package com.gifu.coreservice.model.request;

import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class ChangeWorkflowNameRequest {
    @NotNull
    private Long workflowId;
    @NotEmpty
    private String workflowName;
}
