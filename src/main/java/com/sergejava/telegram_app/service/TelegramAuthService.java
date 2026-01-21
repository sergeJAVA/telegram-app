package com.sergejava.telegram_app.service;

import com.sergejava.telegram_app.dto.UserDTO;

import java.util.Map;

public interface TelegramAuthService {

    /**
     * Метод для валидации метаданных, которые присылает Telegram.
     * @param initData
     * @return {@code true} или {@code false}.
     * @author sergeJAVA
     */
    boolean validateInitData(String initData);

    /**
     * Метод для регистрации нового пользователя в БД.
     * <p>Примечание: использовать только если метаданные от Telegram прошли валидацию.</p>
     * @param params
     * @return {@code UserDTO}.
     * @author sergeJAVA
     */
    UserDTO signUp(Map<String, String> params);

}
