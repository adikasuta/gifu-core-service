package com.gifu.coreservice.config.xenditmerchant;

import lombok.Getter;
import lombok.Setter;

@Getter@Setter
public class MerchantConfig {
    private String prefix;
    private String subprefix;
    private String subsuffix;
    private String from;
    private String until;
}
