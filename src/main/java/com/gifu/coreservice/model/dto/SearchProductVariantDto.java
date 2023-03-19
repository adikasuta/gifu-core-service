package com.gifu.coreservice.model.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class SearchProductVariantDto {
    private Long id;
    private String name;
    private String variantTypeCode;
    private Integer numberOfContent;
    private Integer numberOfUsage;
    private List<String> contentPics;
}
