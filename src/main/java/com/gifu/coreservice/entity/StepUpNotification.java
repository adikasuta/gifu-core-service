package com.gifu.coreservice.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.time.ZonedDateTime;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class StepUpNotification {

    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(
        name = "UUID",
        strategy = "org.hibernate.id.UUIDGenerator"
    )
    private String id;

    private String idBilling;

    private String transactionId;

    @Builder.Default
    private ZonedDateTime received = ZonedDateTime.now();

    private ZonedDateTime acknowledged;
}
