package com.gifu.coreservice.controller.publicapi;

import com.gifu.coreservice.model.dto.ProductSearchDto;
import com.gifu.coreservice.model.dto.dashboard.product.ProductOrderDto;
import com.gifu.coreservice.model.dto.order.product.VariantReferenceDto;
import com.gifu.coreservice.model.request.SearchProductRequest;
import com.gifu.coreservice.model.response.SingleResourceResponse;
import com.gifu.coreservice.service.ProductService;
import com.gifu.coreservice.service.ProductVariantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "public/api/product")
public class PublicProductController {

    @Autowired
    private ProductService productService;
    @Autowired
    private ProductVariantService productVariantService;

    @GetMapping("/{id}")
    public ResponseEntity<SingleResourceResponse<ProductOrderDto>> getDetailProduct(
            @PathVariable Long id
    ) {
        try {
            ProductOrderDto details = productService.getProductById(id);
            return ResponseEntity.ok(new SingleResourceResponse<>(details));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    new SingleResourceResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(), HttpStatus.INTERNAL_SERVER_ERROR.value())
            );
        }
    }

    @GetMapping("/{id}/variants")
    public ResponseEntity<SingleResourceResponse<List<VariantReferenceDto>>> getProductVariants(
            @PathVariable Long id
    ) {
        try {
            List<VariantReferenceDto> variants = productVariantService.getVariantReferenceByProductId(id);
            return ResponseEntity.ok(new SingleResourceResponse<>(variants));
        } catch (Exception ex) {
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
            Pageable pageable
    ) {
        try {
            //TODO: show only product that ready to order for customer
            //not deleted, available and already associated with workflow
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
