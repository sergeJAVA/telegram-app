package com.sergejava.telegram_app.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        Map<String, Object> body = createBody(request, accessDeniedException);
        objectMapper.writeValue(response.getOutputStream(), body);
    }

    private Map<String, Object> createBody(HttpServletRequest request, AccessDeniedException ex) {
        Map<String, Object> body = new HashMap<>();
        body.put("path", request.getServletPath());
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("timestamp", LocalDateTime.now());
        body.put("message", ex.getMessage());
        body.put("method", request.getMethod());
        return body;
    }

}
