package com.gifu.coreservice.model.request;

import lombok.Data;

@Data
public class ChangePasswordRequest {
    private String newPassword;
    private String oldPassword;
}
