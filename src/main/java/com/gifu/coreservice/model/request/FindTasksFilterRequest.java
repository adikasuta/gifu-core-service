package com.gifu.coreservice.model.request;

import lombok.Data;

import java.util.List;

@Data
public class FindTasksFilterRequest {
    private boolean onlyMyTasks;
    private String query;
    private List<String> status;
}
