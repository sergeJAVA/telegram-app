package com.sergejava.telegram_app.entrypoint;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Кастомный entrypoint для не авторизированных пользователей.
 */
@Component
@RequiredArgsConstructor
public class CustomAuthEntrypoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;
    private static final String MESSAGE = "Authentication required or invalid credentials provided.";

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        Map<String, Object> body = createBody(request);
        objectMapper.writeValue(response.getOutputStream(), body);
    }

    private Map<String, Object> createBody(HttpServletRequest request) {
        Map<String, Object> body = new HashMap<>();
        body.put("path", request.getServletPath());
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("timestamp", LocalDateTime.now());
        body.put("message", MESSAGE);
        body.put("method", request.getMethod());
        return body;
    }

}
