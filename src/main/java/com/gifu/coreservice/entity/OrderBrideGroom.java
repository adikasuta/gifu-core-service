package com.gifu.coreservice.entity;

import lombok.Data;

import javax.persistence.*;
import java.time.ZonedDateTime;

@Entity
@Table(name = "order_bride_groom")
@Data
public class OrderBrideGroom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "order_id")
    private Long orderId;
    @Column(name = "bride_name")
    private String brideName;
    @Column(name = "bride_nickname")
    private String brideNickname;
    @Column(name = "bride_father")
    private String brideFather;
    @Column(name = "bride_mother")
    private String brideMother;
    @Column(name = "bride_instagram")
    private String brideInstagram;


    @Column(name = "groom_name")
    private String groomName;
    @Column(name = "groom_nickname")
    private String groomNickname;
    @Column(name = "groom_father")
    private String groomFather;
    @Column(name = "groom_mother")
    private String groomMother;
    @Column(name = "groom_instagram")
    private String groomInstagram;

    @Column(name = "created_date")
    private ZonedDateTime createdDate;
    @Column(name = "updated_date")
    private ZonedDateTime updatedDate;
}
