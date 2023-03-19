package com.gifu.coreservice.model.request;

import lombok.Data;

import java.time.LocalDate;

@Data
public class SaveProfileRequest {
    private Long id;
    private String username;
    private String name;
    private String email;
    private String phoneNo;
    private String address;
    private Integer gender;
    private LocalDate birthDate;
}
