package com.gifu.coreservice.model.dto;

import com.gifu.coreservice.entity.XenditClosedVa;
import lombok.Builder;
import lombok.Data;

import javax.persistence.Column;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
public class CheckoutPaymentDto {
    private Long orderCheckoutPaymentId;
    private Integer sequenceNo;
    private BigDecimal amount;

    private Long activeBillId;
    private ZonedDateTime expiryDate;

    private List<XenditClosedVa> virtualAccounts;
}
