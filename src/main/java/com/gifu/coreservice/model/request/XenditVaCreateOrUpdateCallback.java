package com.gifu.coreservice.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.gifu.coreservice.enumeration.XenditVaStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

@Getter
@Setter
public class XenditVaCreateOrUpdateCallback {

    private String id;

    @JsonProperty("owner_id")
    private String ownerId;

    @JsonProperty("external_id")
    private String externalId;

    @JsonProperty("merchant_code")
    private String merchantCode;

    @JsonProperty("account_number")
    private String accountNumber;

    @JsonProperty("bank_code")
    private String bankCode;

    private String name;

    @JsonProperty("is_closed")
    private boolean isClosed;

    @JsonProperty("is_single_use")
    private boolean isSingleUse;

    private String status;

    @JsonProperty("expiration_date")
    private ZonedDateTime expirationDate;

    private ZonedDateTime updated;

    private ZonedDateTime created;
}
