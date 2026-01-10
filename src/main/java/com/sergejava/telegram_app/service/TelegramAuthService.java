package com.sergejava.telegram_app.service;

import com.sergejava.telegram_app.dto.UserDto;

import java.util.Map;

public interface TelegramAuthService {

    boolean validateInitData(String initData);

    UserDto signUp(Map<String, String> params);

}
