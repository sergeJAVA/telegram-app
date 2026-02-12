package com.sergejava.telegram_app.service;

import com.sergejava.telegram_app.dto.CartDTO;
import com.sergejava.telegram_app.entity.Cart;
import com.sergejava.telegram_app.entity.User;

/**
 * <b>Интерфейс для операций над сущностью {@link Cart}.</b>
 * @author sergeJAVA
 */
public interface CartService {

    /**
     * <b>Метод для получения корзины по {@code userId}.</b>
     * @param userId <p>поле, которое хранится у связанной сущности {@link User} ({@code User.userId}).</p>
     * @return {@link CartDTO}
     */
    CartDTO getCartByUserId(Long userId);

}
