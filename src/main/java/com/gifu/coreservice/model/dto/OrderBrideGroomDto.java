package com.gifu.coreservice.model.dto;

import lombok.Data;

@Data
public class OrderBrideGroomDto {
    private long id;

    private String brideName;
    private String brideNickname;
    private String brideFather;
    private String brideMother;
    private String brideInstagram;

    private String groomName;
    private String groomNickname;
    private String groomFather;
    private String groomMother;
    private String groomInstagram;
}
