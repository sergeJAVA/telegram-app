package com.sergejava.telegram_app.service.impl;

import com.sergejava.telegram_app.dto.UserDTO;
import com.sergejava.telegram_app.entity.Role;
import com.sergejava.telegram_app.exceptions.RoleNotFoundException;
import com.sergejava.telegram_app.exceptions.UserAlreadyExistsException;
import com.sergejava.telegram_app.exceptions.UserNotFoundException;
import com.sergejava.telegram_app.mapper.UserMapper;
import com.sergejava.telegram_app.entity.User;
import com.sergejava.telegram_app.repository.RoleRepository;
import com.sergejava.telegram_app.repository.UserRepository;
import com.sergejava.telegram_app.security.TokenData;
import com.sergejava.telegram_app.service.UserService;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;

    @Override
    @Transactional
    public UserDTO saveUser(@NonNull UserDTO userDto) {
        userRepository.findByUserId(userDto.getUserId()).ifPresent(user -> {
            throw new UserAlreadyExistsException("User with user_id " + user.getUserId() + " already exists.");
        });
        Role userRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RoleNotFoundException("Role USER not found!"));
        User user = UserMapper.toEntity(userDto);
        user.setRoles(Set.of(userRole));
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

    @Override
    public UserDTO addRole(Long id, String role) {
        // TODO: реализовать добавление роли пользователю
        return null;
    }

}
