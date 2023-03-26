package com.gifu.coreservice.model.dto;

import com.gifu.coreservice.entity.Status;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class StepTodoDto {
    private Long id;
    private String name;
    private Long currentStatusId;
    private Long assigneeUserId;
    private List<Status> statuses;
    private OrderDto orderDto;
}
