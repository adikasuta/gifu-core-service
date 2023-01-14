package com.gifu.coreservice.entity;

import Model.PtsV2PaymentsPost201Response;
import Model.PtsV2PaymentsPost201ResponseErrorInformation;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gifu.coreservice.enumeration.CyberSourcePaymentStatus;
import com.gifu.coreservice.enumeration.CyberSourcePaymentStatusReason;
import com.gifu.coreservice.payload.PaymentAuthorizationRequest;
import lombok.*;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Builder
@Entity
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class PaymentResponse {

    @Id
    private String id;

    private String reconciliationId;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private CyberSourcePaymentStatus status;

    private String responseMessage;

    @Builder.Default
    private ZonedDateTime submitted = ZonedDateTime.now();

    @Enumerated(EnumType.STRING)
    private CyberSourcePaymentStatusReason errorReason;

    private String errorMessage;

    private String errorDetails;

    public PaymentResponse(PaymentAuthorizationRequest request,
        PtsV2PaymentsPost201Response ptsV2PaymentsPost201Response) throws JsonProcessingException {

        this.id = ptsV2PaymentsPost201Response.getId();
        this.reconciliationId = ptsV2PaymentsPost201Response.getReconciliationId();
        this.amount = request.getAmount();
        this.status = CyberSourcePaymentStatus.valueOf(ptsV2PaymentsPost201Response.getStatus());
        this.responseMessage = ptsV2PaymentsPost201Response.toString();
        if(ptsV2PaymentsPost201Response.getErrorInformation()!=null){
            PtsV2PaymentsPost201ResponseErrorInformation errorInformation = ptsV2PaymentsPost201Response.getErrorInformation();
            this.errorDetails = new ObjectMapper().writeValueAsString(errorInformation.getDetails());
            this.errorMessage = errorInformation.getMessage();
            this.errorReason = CyberSourcePaymentStatusReason.valueOf(errorInformation.getReason());
        }
    }
}
