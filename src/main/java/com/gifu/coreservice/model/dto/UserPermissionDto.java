package com.gifu.coreservice.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserPermissionDto {
    private Long userId;
    private Long permissionId;
    private String permissionName;
    private boolean granted;
}
