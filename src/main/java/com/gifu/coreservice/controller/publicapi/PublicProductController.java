package com.gifu.coreservice.controller.publicapi;

import com.gifu.coreservice.model.dto.ProductSearchDto;
import com.gifu.coreservice.model.request.SearchProductRequest;
import com.gifu.coreservice.model.response.SingleResourceResponse;
import com.gifu.coreservice.service.ObjectMapperService;
import com.gifu.coreservice.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "api/public/product")
public class PublicProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public ResponseEntity<SingleResourceResponse<Page<ProductSearchDto>>> getSearchProduct(
            @RequestParam(required = false) String searchQuery,
            @RequestParam(required = false) Long productCategoryId,
            @RequestParam(required = false) Long productType,
            Pageable pageable
    ) {
        try {
            SearchProductRequest request = SearchProductRequest.builder()
                    .searchQuery(searchQuery)
                    .productType(productType)
                    .productCategoryId(productCategoryId)
                    .build();
            Page<ProductSearchDto> result = productService.searchProductThenMap(request, pageable);
            return ResponseEntity.ok(new SingleResourceResponse<>(result));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }
}
