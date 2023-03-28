package com.gifu.coreservice.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gifu.coreservice.config.XenditConfiguration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class XenditApiService {

    @Autowired
    private XenditConfiguration xenditConfiguration;
    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    public String doHttpRequest(String url, HttpMethod httpMethod, Object dto) throws JsonProcessingException, HttpStatusCodeException {
        HttpHeaders headers = buildHttpHeaders();

        HttpEntity<String> entity;
        if (dto != null) {
            String requestBody = objectMapper.writeValueAsString(dto);
            log.info("Request Body = {}", requestBody);
            entity = new HttpEntity<>(requestBody, headers);
        } else {
            entity = new HttpEntity<>(headers);
        }

        ResponseEntity<String> responseString;
        String apiHost = xenditConfiguration.getHost();
        String completeUrl = apiHost + url;
        log.info("XenditService {} to url {}", httpMethod.name(), completeUrl);
        try {
            responseString = restTemplate.exchange(
                    completeUrl,
                    httpMethod,
                    entity,
                    String.class
            );

            log.info("XenditService Response : {}", responseString.getBody());

        } catch (HttpStatusCodeException ex) {
            log.error("Xendit API call '{}' failed with message: {}", completeUrl, ex.getMessage(), ex);
            throw ex;
        }

        log.info("Xendit API call '{}' successful with response: {}", completeUrl, responseString.getBody());
        return responseString.getBody();
    }

    private HttpHeaders buildHttpHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBasicAuth(xenditConfiguration.getApiKey(), "");

        return headers;
    }
}
