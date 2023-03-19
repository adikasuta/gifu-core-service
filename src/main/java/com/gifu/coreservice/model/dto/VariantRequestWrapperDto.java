package com.gifu.coreservice.model.dto;

import com.gifu.coreservice.model.request.SaveVariantContentRequest;
import com.gifu.coreservice.model.request.SaveVariantRequest;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class VariantRequestWrapperDto {
    private SaveVariantRequest variant;
    private List<SaveVariantContentRequest> contents;
}
