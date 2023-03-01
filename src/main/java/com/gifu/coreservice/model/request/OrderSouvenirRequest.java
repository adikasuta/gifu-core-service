package com.gifu.coreservice.model.request;

import com.gifu.coreservice.model.dto.CustomerDetailsDto;
import com.gifu.coreservice.model.dto.OrderVariantDto;
import com.gifu.coreservice.model.dto.ShippingDetailsDto;
import lombok.Data;

import java.util.List;

@Data
public class OrderSouvenirRequest {
    private Long productId;
    private List<OrderVariantDto> colors;
    private List<OrderVariantDto> packagingColors;
    private OrderVariantDto packaging;
    private OrderVariantDto additionalVariant;
    private OrderVariantDto emboss;
    private OrderVariantDto size;
    private OrderVariantDto position;
    private OrderVariantDto greetings;
    private String notes;
    private String csReferralToken;
    private CustomerDetailsDto customerDetails;
    private ShippingDetailsDto shippingDetails;
}
