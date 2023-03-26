package com.gifu.coreservice.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StepStatusTodoDto {
    private Long id;
    private String name;
}
