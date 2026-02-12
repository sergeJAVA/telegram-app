package com.sergejava.telegram_app.service;

import com.sergejava.telegram_app.dto.UserDTO;

import java.util.Map;

/**
 * <b>Интерфейс для регистрации/авторизации пользователя <code>Telegram</code></b>
 * @author sergeJAVA
 */
public interface AuthService {

    /**
     * <p><b>Метод для регистрации нового пользователя в БД.</b></p>
     * <br></br>
     * <p><b>Примечание:</b> использовать только если метаданные от <code>Telegram</code> прошли валидацию.</p>
     *
     * @param params
     * @return {@code UserDTO}.
     *
     * @author sergeJAVA
     */
    UserDTO signUp(Map<String, String> params);

    /**
     * <p><b>Метод для авторизации пользователя.</b></p>
     * <br></br>
     * <p><b>Примечание:</b> использовать только если метаданные от <code>Telegram</code> прошли валидацию.</p>
     *
     * @param params
     * @return {@code UserDTO}.
     *
     * @author sergeJAVA
     */
    String signIn(Map<String, String> params);

}
