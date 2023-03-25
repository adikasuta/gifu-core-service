package com.gifu.coreservice.config.xenditmerchant;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "xendit.bri")
@Getter
@Setter
public class BriConfiguration extends MerchantConfig {
}
