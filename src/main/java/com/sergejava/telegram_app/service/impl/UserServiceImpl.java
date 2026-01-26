package com.sergejava.telegram_app.service.impl;

import com.sergejava.telegram_app.dto.UserDTO;
import com.sergejava.telegram_app.exceptions.UserAlreadyExistsException;
import com.sergejava.telegram_app.exceptions.UserNotFoundException;
import com.sergejava.telegram_app.mapper.UserMapper;
import com.sergejava.telegram_app.entity.User;
import com.sergejava.telegram_app.repository.UserRepository;
import com.sergejava.telegram_app.security.TokenData;
import com.sergejava.telegram_app.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    @Transactional
    public UserDTO saveUser(@NonNull UserDTO userDto) {
        userRepository.findByUserId(userDto.getUserId()).ifPresent(user -> {
            throw new UserAlreadyExistsException("User with user_id " + user.getUserId() + " already exists.");
        });
        User user = UserMapper.toEntity(userDto);
        return UserMapper.toDto(userRepository.save(user));
    }

    @Override
    public boolean validatePresence(TokenData tokenData) {
        Optional<User> user = userRepository.findByUserId(tokenData.getUserTelegramId());
        return user.isPresent();
    }

    @Override
    public void deleteUserById(Long id) {
        if (userRepository.findById(id).isEmpty()) {
            log.info("User with ID: {} not found :( ", id);
            throw new UserNotFoundException(id);
        }
        userRepository.deleteById(id);
    }

}
