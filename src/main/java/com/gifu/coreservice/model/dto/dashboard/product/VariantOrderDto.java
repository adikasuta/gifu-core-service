package com.gifu.coreservice.model.dto.dashboard.product;

import com.gifu.coreservice.entity.Content;
import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import java.util.List;
import java.util.Set;

@Data
@Builder
public class VariantOrderDto {
    private Long id;

    private String variantCode;
    private String variantTypeCode;
    private String name;
    private String picture;

    private List<Content> contents;
}
