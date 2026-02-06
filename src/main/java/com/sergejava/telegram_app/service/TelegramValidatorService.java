package com.sergejava.telegram_app.service;

public interface TelegramValidatorService {

    /**
     * Метод для валидации метаданных, которые присылает Telegram.
     *
     * @param initData
     * @return {@code true} или {@code false}.
     *
     * @author sergeJAVA
     */
    boolean validateInitData(String initData);

}
