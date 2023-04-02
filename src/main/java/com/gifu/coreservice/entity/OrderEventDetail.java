package com.gifu.coreservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;

@Entity
@Table(name = "order_event_detail")
@Data
public class OrderEventDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "order_id")
    private Long orderId;
    @Column(name = "name")
    private String name;
    @Column(name = "venue")
    private String venue;
    @Column(name = "date")
    private LocalDate date;
    @Column(name = "time")
    private LocalTime time;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;
    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;
}
