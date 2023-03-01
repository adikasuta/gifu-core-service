package com.gifu.coreservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.ZonedDateTime;

@Entity
@Table(name = "historical_order_status")
@Data
public class HistoricalOrderStatus {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "order_id")
    private Long orderId;
    @Column(name = "status")
    private String status;

    @Column(name = "created_by")
    private String createdBy;
    @Column(name = "created_date")
    private ZonedDateTime createdDate;
}
