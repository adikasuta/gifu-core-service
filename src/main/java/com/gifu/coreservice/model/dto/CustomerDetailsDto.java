package com.gifu.coreservice.model.dto;

import lombok.Data;

import java.time.ZonedDateTime;

@Data
public class CustomerDetailsDto {
    private String name;
    private String email;
    private String phoneNumber;
//    private String brideName;
//    private String groomName;
//    private ZonedDateTime eventDate;
//    private String eventLocation;
}
