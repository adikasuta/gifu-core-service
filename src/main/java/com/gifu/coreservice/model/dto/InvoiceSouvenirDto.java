package com.gifu.coreservice.model.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class InvoiceSouvenirDto {
    private Long orderId;
    private String customerName;
    private String address;
    private String phone;
    private BigDecimal subTotal;
    private BigDecimal shippingFee;
    private BigDecimal chargeFee;
    private BigDecimal cashback;
    private BigDecimal discount;
    private BigDecimal grandTotal;
    private List<SouvenirColorVariantDto> colorPackagingVariants;

    private List<OrderVariantDto> additionalVariant;
    private OrderVariantDto emboss;
    private OrderVariantDto size;
    private OrderVariantDto position;
    private OrderVariantDto greetings;
    private String notes;
    private CustomerDetailsDto customerDetails;
    private ShippingDetailsDto shippingDetails;
}
