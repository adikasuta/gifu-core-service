package com.gifu.coreservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@Entity
@Table(name = "xendit_va_payment")
public class XenditVaPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "xendit_id")
    private String xenditId;

    @Column(name = "va_xendit_id")
    private String vaXenditId;

    @Column(name = "va_external_id")
    private String vaExternalId;

    @Column(name = "bank_code")
    private String bankCode;

    @Column(name = "va_number")
    private String vaNumber;

    @Column(name = "transaction_date")
    private ZonedDateTime transactionDate;

    @Column(name = "amount")
    private BigDecimal amount;

    @Column(name = "bill_id")
    private Long billId;

    @Column(name = "callback_date")
    private ZonedDateTime callbackDate;

    @Column(name = "callback_payload")
    private String callbackPayload;
}