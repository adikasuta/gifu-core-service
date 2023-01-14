package com.gifu.coreservice.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Value("${middleware.allowedFrameAncestors}")
    public String[] allowedFrameAncestors;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .headers()
                .contentSecurityPolicy("frame-ancestors 'self' " + String.join(" ", allowedFrameAncestors));
    }
}
