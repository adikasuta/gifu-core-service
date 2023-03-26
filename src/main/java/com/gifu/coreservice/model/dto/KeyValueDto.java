package com.gifu.coreservice.model.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class KeyValueDto {
    private String key;
    private String value;
}
