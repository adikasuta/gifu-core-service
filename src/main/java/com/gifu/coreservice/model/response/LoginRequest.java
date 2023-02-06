package com.gifu.coreservice.model.response;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class LoginRequest {
    @NotEmpty
    private String email;
    @NotEmpty
    private String password;
    private String redirectUrl;
}
