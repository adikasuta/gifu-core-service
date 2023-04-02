package com.gifu.coreservice.model.request;

import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderCheckoutRequest {
    private List<String> orderCodes;
    private String paymentTermCode;
}
