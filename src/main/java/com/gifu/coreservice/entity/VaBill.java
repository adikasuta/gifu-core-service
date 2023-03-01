package com.gifu.coreservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "va_bill")
@Data
public class VaBill {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "order_checkout_id")
    private Long orderCheckoutId;

    @Column(name = "va_number")
    private String vaNumber;
    @Column(name = "amount")
    private BigDecimal amount;
    @Column(name = "customer_id")
    private Long customerId;
    @Column(name = "customer_email")
    private String customerEmail;
    @Column(name = "payment_date")
    private ZonedDateTime paymentDate;
    @Column(name = "bank_name")
    private String bankName;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;
    @Column(name = "expiry_date")
    private ZonedDateTime expiryDate;
    @Column(name = "remarks")
    private String remarks;
    @Column(name = "created_by")
    private String createdBy;
}
