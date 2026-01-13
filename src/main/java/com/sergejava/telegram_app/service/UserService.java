package com.sergejava.telegram_app.service;

import com.sergejava.telegram_app.dto.UserDto;
import com.sergejava.telegram_app.security.TokenData;

public interface UserService {

    UserDto saveUser(UserDto userDto);

    boolean validatePresence(TokenData tokenData);

}
