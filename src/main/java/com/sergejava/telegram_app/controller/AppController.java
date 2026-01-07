package com.sergejava.telegram_app.controller;

import com.sergejava.telegram_app.dto.InitDataUser;
import com.sergejava.telegram_app.mapper.UserMapper;
import com.sergejava.telegram_app.service.ParseInitDataService;
import com.sergejava.telegram_app.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class AppController {

    private final UserService userService;
    private final ParseInitDataService parseInitDataService;

    @GetMapping("/data")
    public ResponseEntity<?> getData() {
        return ResponseEntity.ok().body(Map.of(
                "message", "Привет! Это данные с сервера",
                "timestamp", LocalDateTime.now()
        ));
    }

    @PostMapping("/user")
    public ResponseEntity<?> saveUser(@RequestBody Map<String, String> request) {
        String initData = request.get("initData");
        try {
            String decodedData = URLDecoder.decode(initData, StandardCharsets.UTF_8);
            InitDataUser initUser = parseInitDataService.userFromInitData(decodedData);
            userService.saveUser(UserMapper.toDto(initUser));
        }catch (Exception ex) {
            return ResponseEntity.status(400).body("Exception: " + ex.getMessage());
        }

        return ResponseEntity.ok().body(Map.of(
                "message", "User data saved successfully",
                "status", "ok"
        ));
    }

}
