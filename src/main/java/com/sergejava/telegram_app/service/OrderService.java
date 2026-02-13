package com.sergejava.telegram_app.service;

import com.sergejava.telegram_app.dto.CreateOrderRequest;
import com.sergejava.telegram_app.dto.OrderDTO;
import com.sergejava.telegram_app.dto.SearchOrdersRequest;
import com.sergejava.telegram_app.entity.Order;
import com.sergejava.telegram_app.security.TokenData;
import org.springframework.data.domain.Page;

/**
 * <b>Интерфейс для операций над сущностью {@link Order}.</b>
 * @author sergeJAVA
 */
public interface OrderService {

    /**
     * <b>Создание нового заказа из товаров в корзине.</b>
     * <br></br>
     * <p><b>Примечание:</b> если корзина пустая, то заказ не будет создан.</p>
     * @param tokenData
     * @param request
     * @return {@link OrderDTO}
     *
     * @author sergeJAVA
     */
    OrderDTO createOrderFromCart(TokenData tokenData, CreateOrderRequest request);

    /**
     * <b>Отмена заказа - установление сущности {@link Order} статуса {@code CANCELLED} и откат зарезервированных товаров в БД.</b>
     *
     * @param orderId ID заказа.
     * @return {@link OrderDTO}
     *
     * @author sergeJAVA
     */
    OrderDTO cancelOrder(Long orderId);

    /**
     * <b>Изменение статуса заказа.</b>
     *
     * @param orderId ID заказа.
     * @param status статус.
     * @return {@link OrderDTO}
     *
     * @author sergeJAVA
     */
    OrderDTO changeStatus(Long orderId, String status);

    OrderDTO cancelMyOrder(Long id, Long userTelegramId);

    Page<OrderDTO> getMyOrders(SearchOrdersRequest request);

}
