package com.gifu.coreservice.enumeration;

import lombok.Getter;

import java.math.BigDecimal;

@Getter
public enum PricingRangeFilter {
    BELLOW_10000("< 10.000", BigDecimal.ZERO, BigDecimal.valueOf(10_000)),
    BETWEEN_10000_to_20000("10.000 ~ 20.000", BigDecimal.valueOf(10_000), BigDecimal.valueOf(20_000)),
    ABOVE_20000("> 20.000", BigDecimal.valueOf(20_000), null);

    private final BigDecimal low;
    private final BigDecimal high;
    private final String text;

    PricingRangeFilter(String text, BigDecimal low, BigDecimal high) {
        this.high = high;
        this.low = low;
        this.text = text;
    }
}
