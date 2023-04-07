package com.gifu.coreservice.model.dto;

import lombok.Builder;
import lombok.Data;

@Builder
@Data
public class PaymentInstructionVADto {
    private String bankCode;
    private String prefix;
    private String fullVaNumber;
    private String accountName;
}
