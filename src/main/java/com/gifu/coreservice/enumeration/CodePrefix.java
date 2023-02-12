package com.gifu.coreservice.enumeration;

import lombok.Getter;

@Getter
public enum CodePrefix {
    WORKFLOW("WF");
    private final String prefix;
    CodePrefix(String prefix){
        this.prefix = prefix;
    }
}
