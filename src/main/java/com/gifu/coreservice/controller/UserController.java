package com.gifu.coreservice.controller;

import com.gifu.coreservice.entity.User;
import com.gifu.coreservice.exception.InvalidRequestException;
import com.gifu.coreservice.model.dto.UserDto;
import com.gifu.coreservice.model.request.SaveProfileRequest;
import com.gifu.coreservice.model.request.SearchUserRequest;
import com.gifu.coreservice.model.response.SingleResourceResponse;
import com.gifu.coreservice.service.ObjectMapperService;
import com.gifu.coreservice.service.UserService;
import com.gifu.coreservice.utils.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(path = "api/user")
public class UserController {

    @Autowired
    private UserService userService;
    @Autowired
    private ObjectMapperService objectMapperService;

    @GetMapping
    public ResponseEntity<SingleResourceResponse<Page<UserDto>>> searchUsers(
            @RequestParam String searchQuery,
            @RequestParam(required = false) Long roleId,
            Pageable pageable
    ) {
        try {
            SearchUserRequest request = SearchUserRequest.builder()
                    .searchQuery(searchQuery)
                    .roleId(roleId)
                    .build();
            Page<UserDto> result = userService.searchUser(request, pageable);
            return ResponseEntity.ok(new SingleResourceResponse<>(result));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }
    @PostMapping(value = "/profile/cs-referral")
    public ResponseEntity<SingleResourceResponse<String>> generateMyCsReferral(
    ) {
        try {
            User user = SessionUtils.getUserContext();
            userService.generateNewReferralCode(user.getId());
            return ResponseEntity.ok(new SingleResourceResponse<>("Success generate new referral code"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }
    @PostMapping(value = "/{userId}/cs-referral")
    public ResponseEntity<SingleResourceResponse<String>> generateCsReferral(
            @PathVariable("userId") Long userId
    ) {
        try {
            userService.generateNewReferralCode(userId);
            return ResponseEntity.ok(new SingleResourceResponse<>("Success generate new referral code"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<SingleResourceResponse<UserDto>> postUser(
            @RequestParam("payload") String payload,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        try {
            User user = SessionUtils.getUserContext();
            SaveProfileRequest request = objectMapperService.readToObject(payload, SaveProfileRequest.class);
            Pair<User, String> result = userService.createUserAccount(request, file, user.getEmail());
            User updated = result.getFirst();
            String generatedPassword = result.getSecond();
            return ResponseEntity.ok(new SingleResourceResponse<>(
                    UserDto.builder()
                            .generatedPassword(generatedPassword)
                            .id(updated.getId())
                            .name(updated.getName())
                            .address(updated.getAddress())
                            .birthDate(updated.getBirthDate())
                            .email(updated.getEmail())
                            .gender(updated.getGender())
                            .username(updated.getUsername())
                            .phoneNo(updated.getPhoneNumber())
                            .build()
            ));
        } catch (InvalidRequestException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new SingleResourceResponse<>(ex.getMessage(), HttpStatus.BAD_REQUEST.value())
            );
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new SingleResourceResponse<>("Inputted username or email had been used", HttpStatus.BAD_REQUEST.value())
            );
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }

    @PatchMapping(value = "/", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<SingleResourceResponse<UserDto>> patchUser(
            @RequestParam("payload") String payload,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        try {
            User user = SessionUtils.getUserContext();
            SaveProfileRequest request = objectMapperService.readToObject(payload, SaveProfileRequest.class);
            User updated = userService.updateUserAccount(request.getId(), request, file, user.getEmail());
            return ResponseEntity.ok(new SingleResourceResponse<>(
                    UserDto.builder()
                            .id(updated.getId())
                            .name(updated.getName())
                            .address(updated.getAddress())
                            .birthDate(updated.getBirthDate())
                            .email(updated.getEmail())
                            .gender(updated.getGender())
                            .username(updated.getUsername())
                            .phoneNo(updated.getPhoneNumber())
                            .build()
            ));
        } catch (InvalidRequestException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new SingleResourceResponse<>(ex.getMessage(), HttpStatus.BAD_REQUEST.value())
            );
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new SingleResourceResponse<>("Inputted username or email had been used", HttpStatus.BAD_REQUEST.value())
            );
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<SingleResourceResponse<UserDto>> getProfile() {
        try {
            User user = SessionUtils.getUserContext();
            UserDto result = userService.getUser(user.getId());
            return ResponseEntity.ok(new SingleResourceResponse<>(result));
        } catch (InvalidRequestException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new SingleResourceResponse<>(ex.getMessage(), HttpStatus.BAD_REQUEST.value())
            );
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }

    @PostMapping(value = "/profile", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<SingleResourceResponse<UserDto>> patchProfile(
            @RequestParam("payload") String payload,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        try {
            User user = SessionUtils.getUserContext();
            SaveProfileRequest request = objectMapperService.readToObject(payload, SaveProfileRequest.class);
            User updated = userService.updateProfile(user.getId(), request, file, user.getEmail());
            return ResponseEntity.ok(new SingleResourceResponse<>(
                    UserDto.builder()
                            .id(updated.getId())
                            .name(updated.getName())
                            .address(updated.getAddress())
                            .birthDate(updated.getBirthDate())
                            .email(updated.getEmail())
                            .gender(updated.getGender())
                            .username(updated.getUsername())
                            .phoneNo(updated.getPhoneNumber())
                            .build()
            ));
        } catch (InvalidRequestException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new SingleResourceResponse<>(ex.getMessage(), HttpStatus.BAD_REQUEST.value())
            );
        } catch (DataIntegrityViolationException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new SingleResourceResponse<>("Inputted username or email had been used", HttpStatus.BAD_REQUEST.value())
            );
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }

    @GetMapping("/ref")
    public ResponseEntity<SingleResourceResponse<List<UserDto>>> getSearchUserRef() {
        try {
            List<UserDto> result = userService.getUserRefs();
            return ResponseEntity.ok(new SingleResourceResponse<>(result));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }
}
