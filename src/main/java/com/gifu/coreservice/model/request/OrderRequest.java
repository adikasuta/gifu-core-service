package com.gifu.coreservice.model.request;

import com.gifu.coreservice.model.dto.*;
import lombok.Data;

import java.util.List;

@Data
public class OrderRequest {
    private Long productId;
    private Integer quantity;
    private String productType;
    private List<OrderVariantDto> variants;
    private String notes;
    private String csReferralToken;
    private OrderBrideGroomDto brideGroom;
    private CustomerDetailsDto customerDetails;
    private ShippingDetailsDto shippingDetails;
    private List<OrderEventDetailDto> eventDetail;
}
