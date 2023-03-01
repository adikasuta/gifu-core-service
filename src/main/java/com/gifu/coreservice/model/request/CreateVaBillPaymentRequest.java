package com.gifu.coreservice.model.request;

import lombok.Data;

@Data
public class CreateVaBillPaymentRequest {
    private Long orderCheckoutId;
    private Integer sequenceNo;
    private String createdBy;
}
