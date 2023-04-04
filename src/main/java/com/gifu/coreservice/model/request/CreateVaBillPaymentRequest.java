package com.gifu.coreservice.model.request;

import lombok.Data;

@Data
public class CreateVaBillPaymentRequest {
    private Long orderCheckoutPaymentId;
    private String createdBy;
}
