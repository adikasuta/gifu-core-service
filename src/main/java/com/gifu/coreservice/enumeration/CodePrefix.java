package com.gifu.coreservice.enumeration;

import lombok.Getter;

@Getter
public enum CodePrefix {
    VARIANT("VR"),
    WORKFLOW("WF"),
    PRODUCT_CATEGORY("PCAT"),
    PRODUCT("PD");
    private final String prefix;
    CodePrefix(String prefix){
        this.prefix = prefix;
    }
}
