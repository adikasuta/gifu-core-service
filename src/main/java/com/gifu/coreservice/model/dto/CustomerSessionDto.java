package com.gifu.coreservice.model.dto;

import lombok.Data;

@Data
public class CustomerSessionDto {
    private String customerName;
    private String customerEmail;
    private String phoneNumber;
}
