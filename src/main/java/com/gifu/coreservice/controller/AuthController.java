package com.gifu.coreservice.controller;

import com.gifu.coreservice.entity.User;
import com.gifu.coreservice.exception.InvalidRequestException;
import com.gifu.coreservice.model.request.ChangePasswordRequest;
import com.gifu.coreservice.model.request.LoginRequest;
import com.gifu.coreservice.model.request.SignupRequest;
import com.gifu.coreservice.model.response.LoginResponse;
import com.gifu.coreservice.model.response.SingleResourceResponse;
import com.gifu.coreservice.service.AuthService;
import com.gifu.coreservice.service.UserService;
import com.gifu.coreservice.utils.SessionUtils;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RequestMapping(path = "auth/api")
public class AuthController {

    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;

    @PostMapping("signup")
    public ResponseEntity<SingleResourceResponse<String>> signup(@RequestBody @Valid SignupRequest request) {
        try {
            userService.signUp(request);
            return ResponseEntity.ok(new SingleResourceResponse<>("success"));
        } catch (InvalidRequestException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new SingleResourceResponse<>(ex.getMessage(), HttpStatus.BAD_REQUEST.value())
            );
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new SingleResourceResponse<>(HttpStatus.UNAUTHORIZED.getReasonPhrase(), HttpStatus.UNAUTHORIZED.value())
            );
        }
    }

    @PostMapping("login")
    public ResponseEntity<SingleResourceResponse<LoginResponse>> login(@RequestBody @Valid LoginRequest request) {
        try {
            LoginResponse response = new LoginResponse();
            String jwt = authService.login(request.getEmail(), request.getPassword());
            response.setToken(jwt);
            return ResponseEntity.ok(new SingleResourceResponse<>(response));
        } catch (Exception ex) {
            log.error("ERROR LOGIN: "+ ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new SingleResourceResponse<>(HttpStatus.UNAUTHORIZED.getReasonPhrase(), HttpStatus.UNAUTHORIZED.value())
            );
        }
    }

    @PostMapping("change-password")
    public ResponseEntity<SingleResourceResponse<String>> changePassword(@RequestBody @Valid ChangePasswordRequest request) {
        try {
            User user = SessionUtils.getUserContext();
            authService.changePassword(user.getEmail(), request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok(new SingleResourceResponse<>("Change password success"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                    new SingleResourceResponse<>(HttpStatus.UNAUTHORIZED.getReasonPhrase(), HttpStatus.UNAUTHORIZED.value())
            );
        }
    }

}