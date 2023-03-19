package com.gifu.coreservice.model.request;

import com.gifu.coreservice.enumeration.VariantType;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SaveVariantRequest {
    private Long id;
    private String name;
    private VariantType variantType;
    private boolean allowedToBeSecondary;
    private String existingPicturePath;
}
