package com.sergejava.telegram_app.service.impl;

import com.sergejava.telegram_app.dto.InitDataUser;
import com.sergejava.telegram_app.dto.UserDTO;
import com.sergejava.telegram_app.entity.User;
import com.sergejava.telegram_app.exceptions.UserNotFoundException;
import com.sergejava.telegram_app.mapper.UserMapper;
import com.sergejava.telegram_app.repository.UserRepository;
import com.sergejava.telegram_app.security.service.JwtService;
import com.sergejava.telegram_app.service.AuthService;
import com.sergejava.telegram_app.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final ObjectMapper objectMapper;
    private final UserRepository userRepository;
    private final JwtService jwtService;

    @Override
    @Transactional
    public UserDTO signUp(Map<String, String> params) {
        InitDataUser initDataUser = objectMapper.readValue(params.get("user"), InitDataUser.class);
        return userService.saveUser(UserMapper.toDto(initDataUser));
    }

    @Override
    @Transactional(readOnly = true)
    public String signIn(Map<String, String> params) {
        InitDataUser initDataUser = objectMapper.readValue(params.get("user"), InitDataUser.class);
        Optional<User> userOptional = userRepository.findByUserId(initDataUser.getId());
        if (userOptional.isEmpty()) {
            throw new UserNotFoundException(initDataUser.getId());
        }
        User user = userOptional.get();
        return jwtService.generateJWT(user);
    }

}
