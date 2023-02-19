package com.gifu.coreservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gifu.coreservice.model.dto.ProductCategoryDto;
import com.gifu.coreservice.model.dto.WorkflowDto;
import com.gifu.coreservice.model.request.SaveProductCategoryRequest;
import com.gifu.coreservice.model.request.SaveWorkflowRequest;
import com.gifu.coreservice.model.response.SingleResourceResponse;
import com.gifu.coreservice.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "api/product-category")
public class ProductCategoryController {

    @Autowired
    private ProductCategoryService productCategoryService;
    @Autowired
    private ObjectMapper objectMapper;

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<SingleResourceResponse<ProductCategoryDto>> postProductCategory(
            @RequestPart("payload") String payload,
            @RequestPart("file") MultipartFile file
    ) {
        try {
            SaveProductCategoryRequest request = objectMapper.readValue(payload, SaveProductCategoryRequest.class);
            ProductCategoryDto result = productCategoryService.createProductCategory(request, file);
            return ResponseEntity.ok(new SingleResourceResponse<>(result));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
