package com.sergejava.telegram_app.service;

import com.sergejava.telegram_app.dto.UserDTO;
import com.sergejava.telegram_app.entity.User;
import com.sergejava.telegram_app.security.TokenData;

/**
 * <b>Интерфейс, предоставляющий методы, для работы с бизнес-логикой над сущностью {@link User}.</b>
 *
 * @author sergeJAVA
 */
public interface UserService {

    /**
     * <b>Метод для сохранения пользователя в БД.</b>
     *
     * @param userDto
     * @return {@link UserDTO}
     *
     * @author sergeJAVA
     */
    UserDTO saveUser(UserDTO userDto);

    /**
     * <b>Метод для проверки того, есть ли пользователь в БД.</b>
     *
     * @param tokenData содержит метаинформацию об аутентифицированном/авторизированном пользователе.
     * @return {@code true} или {@code false}.
     *
     * @author sergeJAVA
     */
    boolean validatePresence(TokenData tokenData);

    void deleteUserById(Long id);

    UserDTO addRole(Long userId, String roleName);

    UserDTO becomeAdmin(TokenData tokenData);

}
