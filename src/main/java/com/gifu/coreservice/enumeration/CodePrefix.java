package com.gifu.coreservice.enumeration;

import lombok.Getter;

@Getter
public enum CodePrefix {
    WORKFLOW("WF"),
    PRODUCT_CATEGORY("PCAT");
    private final String prefix;
    CodePrefix(String prefix){
        this.prefix = prefix;
    }
}
