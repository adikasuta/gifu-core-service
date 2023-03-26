package com.gifu.coreservice.controller;

import com.gifu.coreservice.model.dto.ProductCategoryDto;
import com.gifu.coreservice.model.request.SaveProductCategoryRequest;
import com.gifu.coreservice.model.response.SingleResourceResponse;
import com.gifu.coreservice.service.ProductCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "api/product-category")
public class ProductCategoryController {

    @Autowired
    private ProductCategoryService productCategoryService;

    @PostMapping
    public ResponseEntity<SingleResourceResponse<ProductCategoryDto>> postProductCategory(
            @RequestBody SaveProductCategoryRequest request
    ) {
        try {
            ProductCategoryDto result = productCategoryService.createProductCategory(request, null);
            return ResponseEntity.ok(new SingleResourceResponse<>(result));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }

    @PutMapping
    public ResponseEntity<SingleResourceResponse<ProductCategoryDto>> putProductCategory(
            @RequestBody SaveProductCategoryRequest request
    ) {
        try {
            ProductCategoryDto result = productCategoryService.updateProductCategory(request, null);
            return ResponseEntity.ok(new SingleResourceResponse<>(result));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }

    @GetMapping
    public ResponseEntity<SingleResourceResponse<List<ProductCategoryDto>>> getProductCategory() {
        try {
            List<ProductCategoryDto> result = productCategoryService.getAllProductCategory();
            return ResponseEntity.ok(new SingleResourceResponse<>(result));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SingleResourceResponse<String>> deleteProductCategory(
            @PathVariable("id") Long id
    ) {
        try {
            productCategoryService.deleteProductCategory(id);
            return ResponseEntity.ok(new SingleResourceResponse<>("Delete Success"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }
}
