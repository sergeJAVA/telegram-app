package com.sergejava.telegram_app.service.impl;

import com.sergejava.telegram_app.dto.AddItemToCartRequest;
import com.sergejava.telegram_app.dto.CartItemDTO;
import com.sergejava.telegram_app.entity.Cart;
import com.sergejava.telegram_app.entity.CartItem;
import com.sergejava.telegram_app.entity.Product;
import com.sergejava.telegram_app.mapper.CartItemMapper;
import com.sergejava.telegram_app.repository.CartItemRepository;
import com.sergejava.telegram_app.repository.CartRepository;
import com.sergejava.telegram_app.repository.ProductRepository;
import com.sergejava.telegram_app.security.TokenData;
import com.sergejava.telegram_app.service.CartItemService;
import com.sergejava.telegram_app.service.CartService;
import com.sergejava.telegram_app.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final CartService cartService;
    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final ProductService productService;

    @Override
    @Transactional
    public CartItemDTO addItemToCart(TokenData tokenData, AddItemToCartRequest request) {

        Long cartId = cartService.getCartByUserId(tokenData.getUserTelegramId()).getId();

        Cart cart = cartRepository.findById(cartId).get();

        Product product = productRepository.findProduct(request.getProductId())
                .orElseThrow(() -> new RuntimeException("Product with id " + request.getProductId() + " not found!"));

        productService.changeProductSizeStock(product, request.getProductSize(), request.getQuantity());

        CartItem cartItem = CartItem.builder()
                .price(product.getPrice())
                .quantity(request.getQuantity())
                .cart(cart)
                .product(product)
                .productSize(request.getProductSize())
                .build();

        return CartItemMapper.toDTO(cartItemRepository.save(cartItem));
    }

}
