package com.sergejava.telegram_app.service.impl;

import com.sergejava.telegram_app.service.TelegramValidatorService;
import com.sergejava.telegram_app.service.UserService;
import com.sergejava.telegram_app.util.ParseInitData;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import tools.jackson.databind.ObjectMapper;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.HexFormat;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class TelegramValidatorServiceImpl implements TelegramValidatorService {

    @Value("${telegram.bot.token}")
    private String botToken;

    private final UserService userService;
    private final ObjectMapper objectMapper;

    @Override
    public boolean validateInitData(String initData) {
        try {
            Map<String, String> params = ParseInitData.parseInitData(initData);
            String receivedHash = params.remove("hash");

            String dataCheckString = params.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .map(e -> e.getKey() + "=" + e.getValue())
                    .collect(Collectors.joining("\n"));

            Mac mac = createMacWithKey("WebAppData");
            byte[] secretKeyBytes = mac.doFinal(botToken.getBytes(StandardCharsets.UTF_8));

            byte[] calculatedHash = calculateHash(secretKeyBytes, dataCheckString);

            String calculatedHashHex = HexFormat.of().formatHex(calculatedHash);

            return calculatedHashHex.equals(receivedHash);
        }catch (Exception ex) {
            log.info("Exception message: {}", ex.getMessage());
            return false;
        }
    }

    private Mac createMacWithKey(String secretKey) throws Exception{
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec webAppDataKey = new SecretKeySpec(
                secretKey.getBytes(StandardCharsets.UTF_8),
                "HmacSHA256"
        );
        mac.init(webAppDataKey);
        return mac;
    }

    private byte[] calculateHash(byte[] secretKeyBytes, String dataCheckString) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(secretKeyBytes, "HmacSHA256");
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(secretKey);
        return mac.doFinal(dataCheckString.getBytes(StandardCharsets.UTF_8));
    }

}
