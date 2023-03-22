package com.gifu.coreservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gifu.coreservice.enumeration.ProductType;
import com.gifu.coreservice.model.dto.ProductCategoryDto;
import com.gifu.coreservice.model.dto.ValueTextDto;
import com.gifu.coreservice.model.request.SaveProductCategoryRequest;
import com.gifu.coreservice.model.response.SingleResourceResponse;
import com.gifu.coreservice.service.ProductCategoryService;
import com.gifu.coreservice.service.ProductVariantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(path = "api/ref")
public class ReferenceController {

    @Autowired
    private ProductVariantService productVariantService;

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
}
