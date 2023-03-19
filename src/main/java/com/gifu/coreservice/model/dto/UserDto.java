package com.gifu.coreservice.model.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class UserDto {
    private Long id;
    private String generatedPassword;
    private String username;
    private String name;
    private String email;
    private String phoneNo;
    private String address;
    private Integer gender;
    private LocalDate birthDate;
}
