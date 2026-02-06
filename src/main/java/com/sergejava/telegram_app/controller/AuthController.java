package com.sergejava.telegram_app.controller;

import com.sergejava.telegram_app.dto.InitDataUser;
import com.sergejava.telegram_app.dto.SignUpResponse;
import com.sergejava.telegram_app.dto.SignUpResponseWithUser;
import com.sergejava.telegram_app.dto.UserDTO;
import com.sergejava.telegram_app.security.service.JwtService;
import com.sergejava.telegram_app.service.AuthService;
import com.sergejava.telegram_app.service.TelegramValidatorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
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

    private final TelegramValidatorService telegramValidatorService;
    private final AuthService authService;
    private final JwtService jwtService;
    private final ObjectMapper objectMapper;

    @PostMapping
    public ResponseEntity<?> authenticateTelegram(@RequestBody Map<String, String> request) {
        String initData = request.get("initData");
        if (!telegramValidatorService.validateInitData(initData)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid initData");
        }
        Map<String, String> params = parseInitData(initData);
        InitDataUser initDataUser = objectMapper.readValue(params.get("user"), InitDataUser.class);
        String token = jwtService.generateGuestJWT(initDataUser);
        return ResponseEntity.ok(Map.of("token", token));
    }

    @PutMapping("/signUp")
    public ResponseEntity<?> signUp(@RequestBody Map<String, String> request) {
        String initData = request.get("initData");
        if (!telegramValidatorService.validateInitData(initData)) {
            SignUpResponse response = new SignUpResponse("Invalid initData", HttpStatus.UNAUTHORIZED);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        Map<String, String> params = parseInitData(initData);
        UserDTO userDTO = authService.signUp(params);
        SignUpResponseWithUser response =
                new SignUpResponseWithUser("User data saved successfully", HttpStatus.OK, userDTO);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/signIn")
    public ResponseEntity<?> signIn(@RequestBody Map<String, String> request) {
        String initData = request.get("initData");
        if (!telegramValidatorService.validateInitData(initData)) {
            SignUpResponse response = new SignUpResponse("Invalid initData", HttpStatus.UNAUTHORIZED);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
        Map<String, String> params = parseInitData(initData);
        String token = authService.signIn(params);
        return ResponseEntity.ok(Map.of("token", token));
    }

}
