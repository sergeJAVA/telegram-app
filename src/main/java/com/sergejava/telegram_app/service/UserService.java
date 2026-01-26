package com.sergejava.telegram_app.service;

import com.sergejava.telegram_app.dto.UserDTO;
import com.sergejava.telegram_app.entity.User;
import com.sergejava.telegram_app.security.TokenData;

/**
 * Интерфейс, предоставляющий методы, для работы с бизнес-логикой над сущностью {@link User}.
 *
 * @author sergeJAVA
 */
public interface UserService {

    /**
     * Метод для сохранения пользователя в БД.
     *
     * @param userDto
     * @return {@link UserDTO}
     *
     * @author sergeJAVA
     */
    UserDTO saveUser(UserDTO userDto);

    /**
     * Метод для проверки того, есть ли пользователь в БД.
     *
     * @param tokenData содержит метаинформацию об аутентифицированном/авторизированном пользователе.
     * @return {@code true} или {@code false}.
     *
     * @author sergeJAVA
     */
    boolean validatePresence(TokenData tokenData);

    UserDto saveUser(UserDto userDto);
    void deleteUserById(Long id);
}
