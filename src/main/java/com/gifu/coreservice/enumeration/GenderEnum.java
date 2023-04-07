package com.gifu.coreservice.enumeration;

import lombok.Getter;

@Getter
public enum GenderEnum {
    MALE(1, "Male"), FEMALE(0, "Female");
    private final int code;
    private final String text;
    GenderEnum(int code, String text){
        this.text = text;
        this.code = code;
    }
}
