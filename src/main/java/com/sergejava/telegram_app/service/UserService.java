package com.sergejava.telegram_app.service;

import com.sergejava.telegram_app.dto.UserDTO;
import com.sergejava.telegram_app.security.TokenData;

public interface UserService {

    UserDTO saveUser(UserDTO userDto);

    boolean validatePresence(TokenData tokenData);

}
