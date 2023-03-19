package com.gifu.coreservice.controller;

import com.gifu.coreservice.entity.User;
import com.gifu.coreservice.exception.InvalidRequestException;
import com.gifu.coreservice.model.dto.SearchProductVariantDto;
import com.gifu.coreservice.model.request.SaveVariantContentRequest;
import com.gifu.coreservice.model.request.SaveVariantRequest;
import com.gifu.coreservice.model.request.SearchProductVariantRequest;
import com.gifu.coreservice.model.response.SingleResourceResponse;
import com.gifu.coreservice.service.ObjectMapperService;
import com.gifu.coreservice.service.ProductVariantService;
import com.gifu.coreservice.utils.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = "api/variant")
public class VariantController {

    @Autowired
    private ProductVariantService productVariantService;
    @Autowired
    private ObjectMapperService objectMapperService;

    @GetMapping
    public ResponseEntity<SingleResourceResponse<Page<SearchProductVariantDto>>> getSearchVariants(
            @RequestParam String searchQuery,
            @RequestParam String variantTypeCode,
            Pageable pageable
    ) {
        try {
            SearchProductVariantRequest request = SearchProductVariantRequest.builder()
                    .query(searchQuery)
                    .variantTypeCode(variantTypeCode)
                    .build();
            Page<SearchProductVariantDto> result = productVariantService.searchProductVariants(request, pageable);
            return ResponseEntity.ok(new SingleResourceResponse<>(result));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<SingleResourceResponse<SaveVariantRequest>> getById(
            @PathVariable Long id
    ) {
        try {
            SaveVariantRequest result = productVariantService.findProductVariantsById(id);
            return ResponseEntity.ok(new SingleResourceResponse<>(result));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }

    @PostMapping("/{id}")
    public ResponseEntity<SingleResourceResponse<String>> saveVariant(
            @PathVariable Long id,
            @RequestBody SaveVariantRequest request
    ) {
        try {
            User user = SessionUtils.getUserContext();
            productVariantService.saveVariant(request, null, user.getEmail());
            return ResponseEntity.ok(new SingleResourceResponse<>("Save variant success"));
        } catch (InvalidRequestException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new SingleResourceResponse<>(ex.getMessage(), HttpStatus.BAD_REQUEST.value())
            );
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<SingleResourceResponse<String>> deleteVariant(
            @PathVariable Long id
    ) {
        try {
            productVariantService.deleteVariant(id);
            return ResponseEntity.ok(new SingleResourceResponse<>("Delete variant success"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }

    @PostMapping(value = "/{id}/content", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<SingleResourceResponse<String>> saveContent(
            @PathVariable Long id,
            @RequestPart("payload") String payload,
            @RequestPart("file") MultipartFile file
    ) {
        try {
            User user = SessionUtils.getUserContext();
            SaveVariantContentRequest request = objectMapperService.readToObject(payload, SaveVariantContentRequest.class);
            productVariantService.saveVariantContent(request, file, user.getEmail());
            return ResponseEntity.ok(new SingleResourceResponse<>("Save content success"));
        } catch (InvalidRequestException ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                    new SingleResourceResponse<>(ex.getMessage(), HttpStatus.BAD_REQUEST.value())
            );
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }

    @GetMapping("/{id}/content")
    public ResponseEntity<SingleResourceResponse<Page<SaveVariantContentRequest>>> getContentsByVariantId(
            @PathVariable Long id,
            Pageable pageable
    ) {
        try {
            Page<SaveVariantContentRequest> result = productVariantService.findVariantContentsByVariantId(id, pageable);
            return ResponseEntity.ok(new SingleResourceResponse<>(result));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }

    @DeleteMapping("/{variantId}/content/{id}")
    public ResponseEntity<SingleResourceResponse<String>> deleteContent(
            @PathVariable Long variantId,
            @PathVariable Long id
    ) {
        try {
            productVariantService.deleteVariantContent(id);
            return ResponseEntity.ok(new SingleResourceResponse<>("Delete content success"));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }


}
