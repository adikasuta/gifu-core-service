package com.gifu.coreservice.model.request;

import lombok.Data;

@Data
public class UpdateUserPermissionRequest {
    private Long permissionId;
    private boolean granted;
}
