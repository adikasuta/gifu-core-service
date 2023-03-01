package com.gifu.coreservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Entity
@Table(name = "order_checkout")
@Data
public class OrderCheckout {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @Column(name = "payment_term")
    private String paymentTerm;
    @Column(name = "grand_total")
    private BigDecimal grandTotal;
    @Column(name = "created_date")
    private ZonedDateTime createdDate;
    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;
    @Column(name = "updated_by")
    private String updatedBy;

}
