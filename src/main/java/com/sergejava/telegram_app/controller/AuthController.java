package com.sergejava.telegram_app.controller;

import com.sergejava.telegram_app.dto.InitDataUser;
import com.sergejava.telegram_app.dto.SignUpResponse;
import com.sergejava.telegram_app.security.service.JwtService;
import com.sergejava.telegram_app.service.TelegramAuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

import static com.sergejava.telegram_app.util.ParseInitData.parseInitData;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")
public class AuthController {

    private final TelegramAuthService telegramAuthService;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<?> authenticateTelegram(@RequestBody Map<String, String> request) {
        String initData = request.get("initData");
        if (!telegramAuthService.validateInitData(initData)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid initData");
        }
        Map<String, String> params = parseInitData(initData);
        InitDataUser initDataUser = objectMapper.readValue(params.get("user"), InitDataUser.class);
        String token = jwtService.generateJwt(initDataUser);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PostMapping("/signUp")
    public ResponseEntity<SignUpResponse> signUp(@RequestBody Map<String, String> request) {
        String initData = request.get("initData");
        SignUpResponse response;
        if (!telegramAuthService.validateInitData(initData)) {
            response = new SignUpResponse("Invalid initData", "UNAUTHORIZED");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        Map<String, String> params = parseInitData(initData);
        telegramAuthService.signUp(params);
        response = new SignUpResponse("User data saved successfully", "OK");
        return ResponseEntity.ok(response);
    }

}
