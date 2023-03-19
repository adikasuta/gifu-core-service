package com.gifu.coreservice.model.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SaveVariantContentRequest {
    private Long id;
    private Long variantId;
    private String name;
    private String existingPicturePath;
}
