package com.gifu.coreservice.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class InternalApiServiceImpl implements InternalApiService {

    @Autowired
    private RestTemplate restTemplate;
    private String apiKey;
    private String baseUrl;

    private static final String API_KEY = "x-api-key";

    @Override
    public <T> ResponseEntity<T> post(String url, Object body, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(API_KEY, apiKey);
        HttpEntity<Object> request = new HttpEntity<>(headers);
        if(body!=null){
            request = new HttpEntity<>(body, headers);
        }

        return restTemplate.postForEntity(baseUrl+url, request, responseType);
    }
}
