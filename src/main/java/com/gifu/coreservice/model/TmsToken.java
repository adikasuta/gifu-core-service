package com.gifu.coreservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TmsToken {

    private String customerId;
    private String paymentInstrumentId;
    private String instrumentIdentifierId;
    private String shippingAddressId;
    private String merchantId;
    private String publicKey;
    private String privateKey;
}
