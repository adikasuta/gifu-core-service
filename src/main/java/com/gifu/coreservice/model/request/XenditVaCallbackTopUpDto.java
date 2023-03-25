package com.gifu.coreservice.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class XenditVaCallbackTopUpDto {

    private String amount;

    @JsonProperty("callback_virtual_account_id")
    private String xenditIdForVa;

    @JsonProperty("payment_id")
    private String paymentId;

    @JsonProperty("external_id")
    private String externalId;

    @JsonProperty("account_number")
    private String accountNumber;

    @JsonProperty("merchant_code")
    private String merchantCode;

    @JsonProperty("bank_code")
    private String bankCode;

    @JsonProperty("transaction_timestamp")
    private ZonedDateTime transactionTimestamp;

    private String currency;

    private ZonedDateTime created;

    private ZonedDateTime updated;

    private String id;

    @JsonProperty("owner_id")
    private String ownerId;
}
