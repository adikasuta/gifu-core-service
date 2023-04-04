package com.gifu.coreservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "bill")
@Data
public class Bill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "order_checkout_payment_id")
    private Long orderCheckoutPaymentId;

    @Column(name = "amount")
    private BigDecimal amount;
    @Column(name = "customer_id")
    private Long customerId;
    @Column(name = "customer_email")
    private String customerEmail;
    @Column(name = "customer_name")
    private String customerName;
    @Column(name = "payment_date")
    private ZonedDateTime paymentDate;
    @Column(name = "bill_payment_id")
    private Long billPaymentId;//xenditVaPaymentId
    @Column(name = "payment_partner")
    private String paymentPartner;
    @Column(name="status")
    private String status;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;
    @Column(name = "expiry_date")
    private ZonedDateTime expiryDate;
    @Column(name = "remarks")
    private String remarks;
    @Column(name = "created_by")
    private String createdBy;
}
