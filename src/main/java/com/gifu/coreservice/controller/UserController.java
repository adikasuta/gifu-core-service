package com.gifu.coreservice.controller;

import com.gifu.coreservice.model.dto.UserDto;
import com.gifu.coreservice.model.response.SingleResourceResponse;
import com.gifu.coreservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping("/ref")
    public ResponseEntity<SingleResourceResponse<List<UserDto>>> getSearchUserRef() {
        try {
            List<UserDto> result = userService.getUserRefs();
            return ResponseEntity.ok(new SingleResourceResponse<>(result));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
