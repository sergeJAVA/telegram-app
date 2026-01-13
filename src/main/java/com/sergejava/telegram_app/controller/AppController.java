package com.sergejava.telegram_app.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AppController {

    @GetMapping("/ping")
    public ResponseEntity<?> getData() {
        return ResponseEntity.ok("pong");
    }

}
