package com.gifu.coreservice.controller.publicapi;

import com.gifu.coreservice.enumeration.ProductType;
import com.gifu.coreservice.enumeration.VariantTypeEnum;
import com.gifu.coreservice.model.dto.ValueTextDto;
import com.gifu.coreservice.model.response.SingleResourceResponse;
import com.gifu.coreservice.service.ProductVariantService;
import com.gifu.coreservice.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "public/api/ref")
public class ReferenceController {

    @Autowired
    private ProductVariantService productVariantService;
    @Autowired
    private UserService userService;

    @GetMapping("/product-type")
    public ResponseEntity<SingleResourceResponse<List<ValueTextDto>>> getProductType() {
        List<ValueTextDto> productTypes = List.of(
                new ValueTextDto(ProductType.INVITATION.name(), ProductType.INVITATION.getLabel()),
                new ValueTextDto(ProductType.SOUVENIR.name(), ProductType.SOUVENIR.getLabel())
        );
        return ResponseEntity.ok(new SingleResourceResponse<>(productTypes));
    }

    @GetMapping("/variant-type")
    public ResponseEntity<SingleResourceResponse<List<ValueTextDto>>> getVariantType() {
        List<ValueTextDto> variantTypes = productVariantService.getVariantTypeReference();
        return ResponseEntity.ok(new SingleResourceResponse<>(variantTypes));
    }

    @GetMapping("/variant")
    public ResponseEntity<SingleResourceResponse<List<ValueTextDto>>> getVariants(
    ) {
        List<ValueTextDto> variants = productVariantService.getVariantReference();
        return ResponseEntity.ok(new SingleResourceResponse<>(variants));
    }
    @GetMapping("/variant/{variantTypeCode}")
    public ResponseEntity<SingleResourceResponse<List<ValueTextDto>>> getVariantsByVariantTypeCode(
            @PathVariable VariantTypeEnum variantTypeCode
    ) {
        List<ValueTextDto> variants = productVariantService.getVariantReferenceByVariantTypeCode(variantTypeCode);
        return ResponseEntity.ok(new SingleResourceResponse<>(variants));
    }
    @GetMapping("/roles")
    public ResponseEntity<SingleResourceResponse<List<ValueTextDto>>> getRoles() {
        List<ValueTextDto> roles = userService.getRolesReference();
        return ResponseEntity.ok(new SingleResourceResponse<>(roles));
    }
}
