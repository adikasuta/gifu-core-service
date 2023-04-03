package com.gifu.coreservice.enumeration;

import lombok.Getter;

@Getter
public enum OrderStatus {
    DRAFT("Draft"),
    IN_CART("In Cart"),
    WAITING_FOR_CONFIRMATION("Waiting for Confirmation"),
    WAITING_TO_CREATE_BILL("Waiting to Create Bill"),
    WAITING_FOR_PAYMENT("Waiting for Payment"),
    IN_PROGRESS_PRODUCTION("In Progress Production"),
    DONE("Done");

    private final String label;
    OrderStatus(String label){
        this.label = label;
    }
}
