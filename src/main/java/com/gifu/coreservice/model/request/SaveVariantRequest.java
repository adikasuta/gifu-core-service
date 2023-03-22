package com.gifu.coreservice.model.request;

import com.gifu.coreservice.enumeration.VariantTypeEnum;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SaveVariantRequest {
    private Long id;
    private String name;
    private VariantTypeEnum variantTypeCode;
    private boolean allowedToBeSecondary;
    private String existingPicturePath;
}
