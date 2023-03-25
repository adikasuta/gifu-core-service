package com.gifu.coreservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "xendit_closed_va")
public class XenditClosedVa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "xendit_id")
    private String xenditId;

    @Column(name = "external_id")
    private String externalId;

    @Column(name = "bank_code")
    private String bankCode;

    @Column(name = "va_number")
    private String vaNumber;

    @Column(name = "full_va_number")
    private String fullVaNumber;

    @Column(name = "name")
    private String name;

    @Column(name = "expected_amount")
    private BigDecimal expectedAmount;

    @Column(name = "expiration_date")
    private ZonedDateTime expirationDate;

    @Column(name = "status")
    private String status;

    @Column(name = "bill_id")
    private Long billId;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;
    @Column(name = "response_date")
    private ZonedDateTime responseDate;

    @Column(name = "request_payload")
    private String requestPayload;

    @Column(name = "response_payload")
    private String responsePayload;

    @Column(name = "callback_date")
    private ZonedDateTime callbackDate;
    @Column(name = "callback_payload")
    private String callbackPayload;
}