package com.sergejava.telegram_app.service.impl;

import com.sergejava.telegram_app.dto.CartDTO;
import com.sergejava.telegram_app.entity.Cart;
import com.sergejava.telegram_app.entity.User;
import com.sergejava.telegram_app.exceptions.UserNotFoundException;
import com.sergejava.telegram_app.mapper.CartMapper;
import com.sergejava.telegram_app.repository.CartRepository;
import com.sergejava.telegram_app.repository.UserRepository;
import com.sergejava.telegram_app.service.CartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public CartDTO getCartByUserId(Long userId) {
        Optional<Cart> optionalCart = cartRepository.findUserId(userId);
        if (optionalCart.isPresent()) {
            log.info("Найдена корзина пользователя с userId '{}'.", userId);
            return CartMapper.toDTO(optionalCart.get());
        }
        User user = userRepository.findByUserId(userId).orElseThrow(
                () -> new UserNotFoundException(userId)
        );
        log.info("Корзина с пользователем, у которого userId = {}, не найдена. Cоздается новая корзина.", userId);
        Cart cart = Cart.builder()
                .user(user)
                .build();
        return CartMapper.toDTO(cartRepository.save(cart));
    }

}
