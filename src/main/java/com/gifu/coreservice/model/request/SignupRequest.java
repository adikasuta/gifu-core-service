package com.gifu.coreservice.model.request;

import lombok.Data;

@Data
public class SignupRequest {
    private String email;
    private String password;
    private String confirmPassword;
}
