package com.sergejava.telegram_app.service;

import com.sergejava.telegram_app.dto.AddItemToCartRequest;
import com.sergejava.telegram_app.dto.CartItemDTO;
import com.sergejava.telegram_app.entity.CartItem;
import com.sergejava.telegram_app.security.TokenData;

/**
 * <b>Интерфейс для операций над сущностью {@link CartItem}.</b>
 * @author sergeJAVA
 */
public interface CartItemService {

    /**
     * <b>Метод для добавления {@link CartItem} в корзину.</b>
     *
     * @param tokenData <p>для поиска корзины через поле {@code tokenData.getUserTelegramId()}.</p>
     * @param request запрос с контроллера.
     * @return {@link CartItemDTO}
     *
     * @author sergeJAVA
     */
    CartItemDTO addItemToCart(TokenData tokenData, AddItemToCartRequest request);

    /**
     * <b>Метод для удаления {@link CartItem} из корзины.</b>
     *
     * @param id ID товара в корзине.
     * @param tokenData метаданные из JWT.
     * @author sergeJAVA
     */
    void deleteItemById(Long id, TokenData tokenData);

    /**
     * <p><em><b>Метод для уменьшения количества конкретного {@link CartItem} в корзине.</b></em></p>
     * <br></br>
     * <p>
     * <b><code>Примечание:</code></b> если {@code quantity} будет больше, чем количество товара в корзине, то товар будет удалён из корзины.
     * </p>
     * @param itemId <em>ID товара.</em>
     * @param quantity <em>количество, на которое нужно уменьшить.</em>
     *
     * @return {@link CartItemDTO}
     *
     * @author sergeJAVA
     */
    CartItemDTO reduceItemQuantity(Long itemId, Integer quantity, TokenData tokenData);

    /**
     * <b>Метод для увеличения количества конкретного {@link CartItem} в корзине.</b>
     *
     * @param itemId ID товара.
     * @param quantity количество, на которое нужно уменьшить.
     * @return {@link CartItemDTO}
     *
     * @author sergeJAVA
     */
    CartItemDTO increaseItemQuantity(Long itemId, Integer quantity, TokenData tokenData);

}
