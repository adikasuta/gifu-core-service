package com.gifu.coreservice.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("hello")
public class HelloController {

    @GetMapping
    public ResponseEntity<String> helloWorld() {

        return ResponseEntity.ok("Hello world");
    }

}
