package com.gifu.coreservice.controller;

import com.gifu.coreservice.entity.User;
import com.gifu.coreservice.enumeration.PricingRangeFilter;
import com.gifu.coreservice.exception.InvalidRequestException;
import com.gifu.coreservice.model.dto.ProductSearchDto;
import com.gifu.coreservice.model.request.SaveProductRequest;
import com.gifu.coreservice.model.request.SearchProductRequest;
import com.gifu.coreservice.model.request.UpdateProductStatusRequest;
import com.gifu.coreservice.model.response.SingleResourceResponse;
import com.gifu.coreservice.service.ObjectMapperService;
import com.gifu.coreservice.service.ProductService;
import com.gifu.coreservice.utils.SessionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@Slf4j
@RequestMapping(path = "api/product")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private ObjectMapperService objectMapperService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<SingleResourceResponse<String>> saveProduct(
            @RequestParam("payload") String payload,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        try {
            User user = SessionUtils.getUserContext();
            SaveProductRequest request = objectMapperService.readToObject(payload, SaveProductRequest.class);
            productService.saveProduct(request, file, user.getEmail());
            return ResponseEntity.ok(new SingleResourceResponse<>("Success saving product"));
        } catch (InvalidRequestException ex) {
            log.error("ERROR SAVE PRODUCT, message="+ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new SingleResourceResponse<>(ex.getMessage(), HttpStatus.BAD_REQUEST.value())
            );
        } catch (Exception ex) {
            log.error("ERROR SAVE PRODUCT, message="+ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }

    @GetMapping
    public ResponseEntity<SingleResourceResponse<Page<ProductSearchDto>>> getSearchProduct(
            @RequestParam(required = false) String searchQuery,
            @RequestParam(required = false) Long productCategoryId,
            @RequestParam(required = false) String productType,
            @RequestParam(required = false) PricingRangeFilter pricingRangeFilter,
            Pageable pageable
    ) {
        try {
            SearchProductRequest request = SearchProductRequest.builder()
                    .searchQuery(searchQuery)
                    .productType(productType)
                    .productCategoryId(productCategoryId)
                    .pricingRangeFilter(pricingRangeFilter)
                    .build();
            Page<ProductSearchDto> result = productService.searchProductThenMap(request, pageable);
            return ResponseEntity.ok(new SingleResourceResponse<>(result));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }

    @PatchMapping("{id}/status")
    public ResponseEntity<SingleResourceResponse<String>> patchProductStatus(
            @PathVariable Long id,
            @RequestBody UpdateProductStatusRequest request
            ) {
        try {
            User user = SessionUtils.getUserContext();
            productService.updateStatus(id, request, user.getEmail());
            return ResponseEntity.ok(new SingleResourceResponse<>("Success update product"));
        } catch (InvalidRequestException ex) {
            log.error("ERROR update PRODUCT status, message="+ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new SingleResourceResponse<>(ex.getMessage(), HttpStatus.BAD_REQUEST.value())
            );
        } catch (Exception ex) {
            log.error("ERROR update PRODUCT status, message="+ex.getMessage(), ex);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }
}
