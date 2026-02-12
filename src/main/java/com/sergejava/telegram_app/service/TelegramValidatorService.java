package com.sergejava.telegram_app.service;

/**
 * <b>Интерфейс для валидации метаданных, приходящих от <code>Telegram</code>.</b>
 * @author sergeJAVA
 */
public interface TelegramValidatorService {

    /**
     * <b>Метод для валидации метаданных, которые присылает <code>Telegram</code>.</b>
     *
     * @param initData
     * @return {@code true} или {@code false}.
     *
     * @author sergeJAVA
     */
    boolean validateInitData(String initData);

}
