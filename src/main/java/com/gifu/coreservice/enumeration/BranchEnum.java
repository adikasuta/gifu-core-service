package com.gifu.coreservice.enumeration;

import lombok.Getter;

@Getter
public enum BranchEnum {

    JAKARTA("J");
    private final String code;
    BranchEnum(String code){
        this.code = code;
    }
}
