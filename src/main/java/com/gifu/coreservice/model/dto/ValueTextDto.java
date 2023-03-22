package com.gifu.coreservice.model.dto;

import lombok.Data;

@Data
public class ValueTextDto {
    public ValueTextDto() {

    }

    public ValueTextDto(String value, String text) {
        this.value = value;
        this.text = text;
    }

    public ValueTextDto(String value, String text, String meta) {
        this.value = value;
        this.text = text;
        this.meta = meta;
    }

    private String value;
    private String text;
    private String meta;
}
