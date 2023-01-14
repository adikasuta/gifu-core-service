package com.gifu.coreservice.service;

import org.springframework.http.ResponseEntity;

public interface InternalApiService {

    <T> ResponseEntity<T> post(String url, Object body, Class<T> responseType);
}
