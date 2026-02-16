package com.sergejava.telegram_app.service.impl;

import com.sergejava.telegram_app.dto.AddItemToCartRequest;
import com.sergejava.telegram_app.dto.CartItemDTO;
import com.sergejava.telegram_app.entity.Cart;
import com.sergejava.telegram_app.entity.CartItem;
import com.sergejava.telegram_app.entity.Product;
import com.sergejava.telegram_app.entity.ProductSize;
import com.sergejava.telegram_app.entity.User;
import com.sergejava.telegram_app.exceptions.CartItemNotFoundException;
import com.sergejava.telegram_app.exceptions.CartItemRemovedException;
import com.sergejava.telegram_app.exceptions.CartOwnershipException;
import com.sergejava.telegram_app.exceptions.InsufficientStockException;
import com.sergejava.telegram_app.exceptions.ProductNotFoundException;
import com.sergejava.telegram_app.exceptions.UserNotFoundException;
import com.sergejava.telegram_app.mapper.CartItemMapper;
import com.sergejava.telegram_app.repository.CartItemRepository;
import com.sergejava.telegram_app.repository.CartRepository;
import com.sergejava.telegram_app.repository.ProductRepository;
import com.sergejava.telegram_app.repository.UserRepository;
import com.sergejava.telegram_app.security.TokenData;
import com.sergejava.telegram_app.service.CartItemService;
import com.sergejava.telegram_app.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartItemServiceImpl implements CartItemService {

    private final CartItemRepository cartItemRepository;
    private final CartRepository cartRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductService productService;
    private final CacheManager cacheManager;

    @Override
    @Transactional
    @CacheEvict(value = "cart", key = "#tokenData.userTelegramId")
    public CartItemDTO addItemToCart(TokenData tokenData, AddItemToCartRequest request) {
        Cart cart = findCartElseCreate(tokenData);

        Product product = productRepository.findProduct(request.getProductId())
                .orElseThrow(() -> new ProductNotFoundException(request.getProductId()));

        ProductSize productSize = productService
                .reduceProductSizeStock(product, request.getProductSize(), request.getQuantity());

        Optional<CartItem> existingCartItem = cartItemRepository.findByProductAndSizeAndCart(
                product.getName(),
                productSize.getSize().getName(),
                cart.getId());

        if (existingCartItem.isPresent()) {
            CartItem cartItem = increaseQuantity(existingCartItem.get(), request, cart);
            return CartItemMapper.toDTO(cartItem);
        }

        CartItem cartItem = createNew(request, product, cart, productSize);

        cart.setUpdatedAt(LocalDateTime.now());
        return CartItemMapper.toDTO(cartItemRepository.save(cartItem));
    }

    @Override
    @Transactional
    public void deleteItemById(Long id, TokenData tokenData) {
        CartItem cartItem = cartItemRepository.findCartItemById(id)
                .orElseThrow(() -> new CartItemNotFoundException(id));
        Long userId = cartItem.getCart().getUser().getUserId();
        if (!userId.equals(tokenData.getUserTelegramId())) {
            throw CartOwnershipException.defaultMessage();
        }
        increaseProductSizeAndProductStock(cartItem, cartItem.getQuantity());
        clearCartCache(userId);
        cartItemRepository.deleteById(id);
    }

    @Override
    @Transactional(noRollbackFor = CartItemRemovedException.class)
    public CartItemDTO reduceItemQuantity(Long itemId, Integer requestQuantity, TokenData tokenData) {
        CartItem cartItem = cartItemRepository.findCartItemById(itemId)
                .orElseThrow(() -> new CartItemNotFoundException(itemId));
        validateCartOwnership(cartItem.getCart().getUser(), tokenData);
        Integer itemQuantity = cartItem.getQuantity();
        if (itemQuantity == 1) {
            clearCartCache(cartItem.getCart().getUser().getUserId());
            removeAndThrow(cartItem, 1);
        } else if (!validatedQuantity(itemQuantity, requestQuantity)) {
            clearCartCache(cartItem.getCart().getUser().getUserId());
            removeAndThrow(cartItem, cartItem.getQuantity());
        }

        cartItem.setQuantity(itemQuantity - requestQuantity);
        increaseProductSizeAndProductStock(cartItem, requestQuantity);
        clearCartCache(cartItem.getCart().getUser().getUserId());
        return CartItemMapper.toDTO(cartItem);
    }

    @Override
    @Transactional
    public CartItemDTO increaseItemQuantity(Long itemId, Integer quantity, TokenData tokenData) {
        CartItem cartItem = cartItemRepository.findCartItemById(itemId)
                .orElseThrow(() -> new CartItemNotFoundException(itemId));
        validateCartOwnership(cartItem.getCart().getUser(), tokenData);
        Integer itemQuantity = cartItem.getQuantity();

        reduceProductSizeAndProductStock(cartItem, quantity);

        cartItem.setQuantity(itemQuantity + quantity);
        clearCartCache(cartItem.getCart().getUser().getUserId());
        return CartItemMapper.toDTO(cartItem);
    }

    private void validateCartOwnership(User user, TokenData tokenData) {
        if (!user.getUserId().equals(tokenData.getUserTelegramId())) {
            throw new CartOwnershipException("The CartItem you are trying to change is in a cart that doesn't belong to you!");
        }
    }

    private void removeAndThrow(CartItem cartItem, Integer quantity) {
        increaseProductSizeAndProductStock(cartItem, quantity);
        cartItemRepository.deleteById(cartItem.getId());
        throw new CartItemRemovedException(cartItem.getId());
    }

    private boolean validatedQuantity(Integer itemQuantity, Integer requestQuantity) {
        return (itemQuantity - requestQuantity) > 0;
    }

    private void increaseProductSizeAndProductStock(CartItem cartItem, Integer quantity) {
        Product product = cartItem.getProduct();
        ProductSize productSize = cartItem.getProductSize();

        Integer productStock = product.getStock();
        Integer productSizeStock = productSize.getStock();

        product.setStock(productStock + quantity);
        productSize.setStock(productSizeStock + quantity);
    }

    private void reduceProductSizeAndProductStock(CartItem cartItem, Integer quantity) {
        Product product = cartItem.getProduct();
        ProductSize productSize = cartItem.getProductSize();

        Integer productStock = product.getStock();
        Integer productSizeStock = productSize.getStock();
        if (productStock == 0) {
            throw new InsufficientStockException("the stock of the product is 0.");
        } else if (productStock < quantity) {
            throw new InsufficientStockException("The quantity of the requested product is greater than the stock.");
        } else if (productSizeStock == 0) {
            throw new InsufficientStockException("The stock of the required product size is 0.");
        } else if (productSizeStock < quantity) {
            throw new InsufficientStockException("The requested quantity is greater than the stock size!");
        } else {
            product.setStock(productStock - quantity);
            productSize.setStock(productSizeStock - quantity);
        }
    }

    private CartItem createNew(AddItemToCartRequest request,
                               Product product,
                               Cart cart,
                               ProductSize productSize) {
        return CartItem.builder()
                .price(product.getPrice())
                .quantity(request.getQuantity())
                .cart(cart)
                .product(product)
                .productSize(productSize)
                .build();
    }

    private CartItem increaseQuantity(CartItem cartItem, AddItemToCartRequest request, Cart cart) {
        Integer quantity = cartItem.getQuantity();
        cartItem.setQuantity(quantity + request.getQuantity());
        cart.setUpdatedAt(LocalDateTime.now());
        return cartItem;
    }

    private void clearCartCache(Long userId) {
        cacheManager.getCache("cart").evict(userId);
    }

    private Cart findCartElseCreate(TokenData tokenData) {
        Optional<Cart> optionalCart = cartRepository.findByUserId(tokenData.getUserTelegramId());
        if (optionalCart.isEmpty()) {
            log.info("A NEW CART IS CREATED BECAUSE IT DID NOT EXIST FOR THE USER");
            User user = userRepository.findByUserId(tokenData.getUserTelegramId()).orElseThrow(
                    () -> new UserNotFoundException(tokenData.getUserTelegramId())
            );
            Cart cart = Cart.builder()
                    .user(user)
                    .build();
            return cartRepository.save(cart);
        }
        log.info("CART FOUND");
        return optionalCart.get();
    }

}
