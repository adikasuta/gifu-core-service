package com.gifu.coreservice.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "xendit")
@Getter
@Setter
public class XenditConfiguration {
    private String host;
    private String apiKey;
    private String callbackKey;
    private String dynamicVaLength;
}
