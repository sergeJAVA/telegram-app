package com.sergejava.telegram_app.service.impl;

import com.sergejava.telegram_app.dto.UserDto;
import com.sergejava.telegram_app.exceptions.UserAlreadyExistsException;
import com.sergejava.telegram_app.mapper.UserMapper;
import com.sergejava.telegram_app.entity.User;
import com.sergejava.telegram_app.repository.UserRepository;
import com.sergejava.telegram_app.security.TokenData;
import com.sergejava.telegram_app.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDto saveUser(@NonNull UserDto userDto) {
        userRepository.findByUserId(userDto.getUserId()).ifPresent(user -> {
            throw new UserAlreadyExistsException("User with user_id " + user.getUserId() + " already exists.");
        });
        User user = UserMapper.toEntity(userDto);
        return UserMapper.toDto(userRepository.save(user));
    }

    @Override
    public boolean validatePresence(TokenData tokenData) {
        Optional<User> user = userRepository.findByUserId(tokenData.getUserId());
        return user.isPresent();
    }

}
