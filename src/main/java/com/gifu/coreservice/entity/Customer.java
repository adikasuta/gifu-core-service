package com.gifu.coreservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.ZonedDateTime;

@Entity
@Table(name = "customer")
@Data
public class Customer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "phone_number")
    private String phoneNumber;
    @Column(name = "name")
    private String name;
    @Column(name = "address")
    private String address;
    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;
    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;
}
