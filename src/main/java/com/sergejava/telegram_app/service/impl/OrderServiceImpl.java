package com.sergejava.telegram_app.service.impl;

import com.sergejava.telegram_app.constant.OrderStatus;
import com.sergejava.telegram_app.dto.CreateOrderRequest;
import com.sergejava.telegram_app.dto.OrderDTO;
import com.sergejava.telegram_app.entity.Cart;
import com.sergejava.telegram_app.entity.CartItem;
import com.sergejava.telegram_app.entity.Order;
import com.sergejava.telegram_app.entity.OrderItem;
import com.sergejava.telegram_app.entity.Product;
import com.sergejava.telegram_app.entity.ProductSize;
import com.sergejava.telegram_app.exceptions.CartNotFoundException;
import com.sergejava.telegram_app.exceptions.EmptyCartException;
import com.sergejava.telegram_app.exceptions.InvalidOrderStatusException;
import com.sergejava.telegram_app.exceptions.OrderAlreadyCancelledException;
import com.sergejava.telegram_app.exceptions.OrderNotFoundException;
import com.sergejava.telegram_app.mapper.OrderMapper;
import com.sergejava.telegram_app.repository.CartRepository;
import com.sergejava.telegram_app.repository.OrderItemRepository;
import com.sergejava.telegram_app.repository.OrderRepository;
import com.sergejava.telegram_app.repository.ProductRepository;
import com.sergejava.telegram_app.security.TokenData;
import com.sergejava.telegram_app.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final CartRepository cartRepository;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductRepository productRepository;

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

    @Override
    @Transactional
    public OrderDTO cancelOrder(Long orderId) {
        Order order = orderRepository.findByIdWithAllLinks(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        if (isOrderCancelled(order)) {
            throw new OrderAlreadyCancelledException(
                    String.format("Order with ID '%d' has already been cancelled.", orderId)
            );
        }
        order.setStatus(OrderStatus.CANCELLED);
        refundProducts(order.getOrderItems());
        return OrderMapper.toDTO(order);
    }

    @Override
    @Transactional
    public OrderDTO changeStatus(Long orderId, String status) {
        Order order = orderRepository.findByIdWithAllLinks(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
        if (isOrderCancelled(order)) {
            throw new OrderAlreadyCancelledException(
                    String.format("The order with ID '%d' has already been canceled." +
                            " You cannot change its status.", orderId));
        }
        changeOrderStatus(order, status);
        return OrderMapper.toDTO(order);
    }

    private void changeOrderStatus(Order order, String status) {
        switch (status.toUpperCase(Locale.ROOT)) {
            case "PENDING" -> order.setStatus(OrderStatus.PENDING);
            case "PAID" -> order.setStatus(OrderStatus.PAID);
            case "SHIPPED" -> order.setStatus(OrderStatus.SHIPPED);
            case "DELIVERED" -> order.setStatus(OrderStatus.DELIVERED);
            case "CANCELLED" -> throw new InvalidOrderStatusException("Invalid order status." +
                    " You can't set the status to CANCELLED; there's a different endpoint for that.\n" +
                    "Valid values: PENDING, PAID, SHIPPED, DELIVERED.");
            default -> throw new InvalidOrderStatusException("Invalid order status." +
                    " Valid values: PENDING, PAID, SHIPPED, DELIVERED.");
        }
    }

    private void refundProducts(Set<OrderItem> orderItems) {
        for (OrderItem orderItem : orderItems) {
            Product product = orderItem.getProduct();
            product.getProductSizes().stream()
                    .filter(productSize -> sameName(productSize, orderItem))
                    .forEach(productSize -> {
                        Integer sizeStock = productSize.getStock();
                        Integer productStock = product.getStock();
                        Integer orderItemQuantity = orderItem.getQuantity();

                        productSize.setStock(sizeStock + orderItemQuantity);
                        product.setStock(productStock + orderItemQuantity);
                    });
        }
    }

    private boolean sameName(ProductSize productSize, OrderItem orderItem) {
        return productSize.getSize().getName().equals(orderItem.getProductSize());
    }

    private boolean isOrderCancelled(Order order) {
        return order.getStatus().equals(OrderStatus.CANCELLED);
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
