package com.sergejava.telegram_app.service;

import com.sergejava.telegram_app.dto.UserDTO;

import java.util.Map;

public interface TelegramAuthService {

    boolean validateInitData(String initData);

    UserDTO signUp(Map<String, String> params);

}
