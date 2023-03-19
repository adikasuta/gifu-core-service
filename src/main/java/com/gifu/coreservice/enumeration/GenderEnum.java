package com.gifu.coreservice.enumeration;

import lombok.Getter;

@Getter
public enum GenderEnum {
    MALE(1), FEMALE(0);
    private final int code;
    GenderEnum(int code){
        this.code = code;
    }
}
