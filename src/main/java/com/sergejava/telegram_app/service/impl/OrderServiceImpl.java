package com.sergejava.telegram_app.service.impl;

import com.sergejava.telegram_app.constant.OrderStatus;
import com.sergejava.telegram_app.dto.CreateOrderRequest;
import com.sergejava.telegram_app.dto.OrderDTO;
import com.sergejava.telegram_app.entity.Cart;
import com.sergejava.telegram_app.entity.CartItem;
import com.sergejava.telegram_app.entity.Order;
import com.sergejava.telegram_app.entity.OrderItem;
import com.sergejava.telegram_app.exceptions.CartNotFoundException;
import com.sergejava.telegram_app.exceptions.EmptyCartException;
import com.sergejava.telegram_app.mapper.OrderMapper;
import com.sergejava.telegram_app.repository.CartRepository;
import com.sergejava.telegram_app.repository.OrderItemRepository;
import com.sergejava.telegram_app.repository.OrderRepository;
import com.sergejava.telegram_app.security.TokenData;
import com.sergejava.telegram_app.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;

    @Override
    @Transactional
    public OrderDTO createOrderFromCart(TokenData tokenData, CreateOrderRequest request) {
        Cart cart = cartRepository.findByUserId(tokenData.getUserTelegramId())
                .orElseThrow(() -> new CartNotFoundException("The Cart for the user with userId '"
                        + tokenData.getUserTelegramId() + "' was not found. Most likely, the user is not registered."));
        if (cart.getCartItems().isEmpty()) {
            throw new EmptyCartException();
        }
        Order order = createOrder(cart, request);
        Order savedOrder = orderRepository.save(order);

        Set<OrderItem> orderItems = createOrderItems(cart.getCartItems(), savedOrder);
        savedOrder.setOrderItems(orderItems);

        orderRepository.save(savedOrder);
        clearCart(cart);
        return OrderMapper.toDTO(savedOrder);
    }

    private Set<OrderItem> createOrderItems(Set<CartItem> cartItems, Order order) {
        Set<OrderItem> orderItems = new HashSet<>();
        for (CartItem cartItem : cartItems) {
            OrderItem orderItem = createOrderItemFromCartItem(cartItem, order);
            orderItems.add(orderItem);
        }
        return orderItems;
    }

    private void clearCart(Cart cart) {
        cart.getCartItems().clear();
        cart.setUpdatedAt(LocalDateTime.now());
    }

    private BigDecimal calculateTotal(Cart cart) {
        return cart.getCartItems().stream()
                .map(item -> item.getPrice().multiply(new BigDecimal(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private Order createOrder(Cart cart, CreateOrderRequest request) {
        return Order.builder()
                .status(OrderStatus.PENDING)
                .deliveryAddress(request.getDeliveryAddress())
                .phoneNumber(request.getPhoneNumber())
                .totalPrice(calculateTotal(cart))
                .user(cart.getUser())
                .build();
    }

    private OrderItem createOrderItemFromCartItem(CartItem cartItem, Order order) {
        return OrderItem.builder()
                .quantity(cartItem.getQuantity())
                .price(cartItem.getPrice())
                .product(cartItem.getProduct())
                .productName(cartItem.getProduct().getName())
                .productSize(cartItem.getProductSize().getSize().getName())
                .imageURL(cartItem.getProduct().getMainProductImage().getUrl())
                .order(order)
                .build();
    }

}
