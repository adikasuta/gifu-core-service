package com.gifu.coreservice.controller;

import com.gifu.coreservice.entity.User;
import com.gifu.coreservice.exception.InvalidRequestException;
import com.gifu.coreservice.model.dto.ProductSearchDto;
import com.gifu.coreservice.model.request.SaveProductRequest;
import com.gifu.coreservice.model.request.SearchProductRequest;
import com.gifu.coreservice.model.response.SingleResourceResponse;
import com.gifu.coreservice.service.ObjectMapperService;
import com.gifu.coreservice.service.ProductService;
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
@RequestMapping(path = "api/product")
public class ProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private ObjectMapperService objectMapperService;

    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<SingleResourceResponse<String>> saveProduct(
            @RequestPart("payload") String payload,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        try {
            User user = SessionUtils.getUserContext();
            SaveProductRequest request = objectMapperService.readToObject(payload, SaveProductRequest.class);
            productService.saveProduct(request, file, user.getEmail());
            return ResponseEntity.ok(new SingleResourceResponse<>("Success saving product"));
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
}
