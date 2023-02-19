package com.gifu.coreservice.enumeration;

import lombok.Getter;

@Getter
public enum ProductType {
    SOUVENIR("Souvenir"),
    INVITATION("Invitation");
    private final String label;
    ProductType(String label){
        this.label = label;
    }
}
