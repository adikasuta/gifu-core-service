package com.gifu.coreservice.controller;

import com.gifu.coreservice.entity.User;
import com.gifu.coreservice.model.request.ChangePasswordRequest;
import com.gifu.coreservice.model.request.LoginRequest;
import com.gifu.coreservice.model.response.LoginResponse;
import com.gifu.coreservice.model.response.SingleResourceResponse;
import com.gifu.coreservice.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("login")
    public ResponseEntity<SingleResourceResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest request) {
        try {
            LoginResponse response = new LoginResponse();
            String jwt = authService.login(request.getEmail(), request.getPassword());
            response.setToken(jwt);
            return ResponseEntity.ok(new SingleResourceResponse<>(response));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("change-password")
    public ResponseEntity<SingleResourceResponse> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        try {
            User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            authService.changePassword(user.getEmail(), request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok(new SingleResourceResponse<>("Change password success"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

}