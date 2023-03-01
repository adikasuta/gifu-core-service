package com.gifu.coreservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_checkout_payment")
@Data
public class OrderCheckoutPayment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "order_checkout_id")
    private Long orderCheckoutId;
    @Column(name = "sequence_no")
    private Integer sequenceNo;
    @Column(name = "amount")
    private BigDecimal amount;
}
