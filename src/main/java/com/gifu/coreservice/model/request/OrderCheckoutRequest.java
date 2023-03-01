package com.gifu.coreservice.model.request;

import lombok.Data;

import java.util.List;

@Data
public class OrderCheckoutRequest {
    private List<Long> orderIds;
    private String paymentTermCode;
}
