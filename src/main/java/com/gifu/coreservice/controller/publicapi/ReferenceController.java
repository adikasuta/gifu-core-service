package com.gifu.coreservice.controller.publicapi;

import com.gifu.coreservice.enumeration.OrderStatus;
import com.gifu.coreservice.enumeration.ProductType;
import com.gifu.coreservice.enumeration.VariantTypeEnum;
import com.gifu.coreservice.model.dto.ValueTextDto;
import com.gifu.coreservice.model.response.SingleResourceResponse;
import com.gifu.coreservice.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping(path = "public/api/ref")
public class ReferenceController {

    @Autowired
    private ProductVariantService productVariantService;
    @Autowired
    private ProductCategoryService productCategoryService;
    @Autowired
    private UserService userService;
    @Autowired
    private AdministrativeAreaService administrativeAreaService;

    @GetMapping("/order-status")
    public ResponseEntity<SingleResourceResponse<List<ValueTextDto>>> getOrderStatus() {
        List<ValueTextDto> options = new ArrayList<>();
        for(OrderStatus status : List.of(
                OrderStatus.WAITING_FOR_CONFIRMATION,
                OrderStatus.WAITING_TO_CREATE_BILL,
                OrderStatus.WAITING_FOR_PAYMENT,
                OrderStatus.IN_PROGRESS_PRODUCTION,
                OrderStatus.DONE)){
            options.add(new ValueTextDto(status.name(), status.getLabel()));
        }
        return ResponseEntity.ok(new SingleResourceResponse<>(options));
    }

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

    @GetMapping("/product-categories")
    public ResponseEntity<SingleResourceResponse<List<ValueTextDto>>> getProductCategories() {
        List<ValueTextDto> options = productCategoryService.getProductCategoryOptions();
        return ResponseEntity.ok(new SingleResourceResponse<>(options));
    }

    @GetMapping("/provinces")
    public ResponseEntity<SingleResourceResponse<List<ValueTextDto>>> getProvinces() {
        List<ValueTextDto> options = administrativeAreaService.getProvinceReference();
        return ResponseEntity.ok(new SingleResourceResponse<>(options));
    }
    @GetMapping("/provinces/{provinceId}/cities")
    public ResponseEntity<SingleResourceResponse<List<ValueTextDto>>> getCities(
            @PathVariable String provinceId
    ) {
        List<ValueTextDto> options = administrativeAreaService.getCityReference(provinceId);
        return ResponseEntity.ok(new SingleResourceResponse<>(options));
    }

    @GetMapping("/cities/{cityId}/districts")
    public ResponseEntity<SingleResourceResponse<List<ValueTextDto>>> getDistrict(
            @PathVariable String cityId
    ) {
        List<ValueTextDto> options = administrativeAreaService.getDistrictReference(cityId);
        return ResponseEntity.ok(new SingleResourceResponse<>(options));
    }

    @GetMapping("/districts/{districtId}/kelurahan")
    public ResponseEntity<SingleResourceResponse<List<ValueTextDto>>> getKelurahan(
            @PathVariable String districtId
    ) {
        List<ValueTextDto> options = administrativeAreaService.getKelurahanReference(districtId);
        return ResponseEntity.ok(new SingleResourceResponse<>(options));
    }

    @GetMapping("/shipping-vendors")
    public ResponseEntity<SingleResourceResponse<List<ValueTextDto>>> getShippingVendors(
    ) {
        List<ValueTextDto> options = administrativeAreaService.getShippingVendors();
        return ResponseEntity.ok(new SingleResourceResponse<>(options));
    }
}
