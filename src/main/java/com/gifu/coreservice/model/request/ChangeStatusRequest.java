package com.gifu.coreservice.model.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ChangeStatusRequest {
    private Long orderId;
    private String status;
    private String updaterEmail;
}
