package com.gifu.coreservice.enumeration;

import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
public enum ShippingVendor {
    SELF_PICKUP("Self Pickup"),
    GOJEK_GRAB("Gojek/Grab Send"),
    SICEPAT("Sicepat"),
    INDAH_CARGO("Indah Cargo");

    private String text;

    ShippingVendor(String text) {
        this.text = text;
    }
}
