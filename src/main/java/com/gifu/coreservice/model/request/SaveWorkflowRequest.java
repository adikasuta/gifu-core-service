package com.gifu.coreservice.model.request;

import com.gifu.coreservice.model.dto.StepDto;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
public class SaveWorkflowRequest {
    private Long id;
    @NotEmpty
    private String name;
    @NotEmpty
    private List<Long> productCategoryIds;
    @NotEmpty
    private List<StepDto> steps;
}
