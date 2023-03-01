package com.gifu.coreservice.enumeration;

import lombok.Getter;

@Getter
public enum ProductType {
    SOUVENIR("Souvenir", "S"),
    INVITATION("Invitation", "I");
    private final String label;
    private final String invoiceCode;
    ProductType(String label, String invoiceCode){
        this.label = label;
        this.invoiceCode = invoiceCode;
    }
}
