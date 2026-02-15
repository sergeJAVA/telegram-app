package com.sergejava.telegram_app.controller;

import com.sergejava.telegram_app.dto.AddItemToCartRequest;
import com.sergejava.telegram_app.dto.CartDTO;
import com.sergejava.telegram_app.dto.CategoryDTO;
import com.sergejava.telegram_app.dto.CreateProductRequest;
import com.sergejava.telegram_app.dto.ProductDTO;
import com.sergejava.telegram_app.dto.UserDTO;
import com.sergejava.telegram_app.entity.Role;
import com.sergejava.telegram_app.entity.User;
import com.sergejava.telegram_app.mapper.UserMapper;
import com.sergejava.telegram_app.repository.CategoryRepository;
import com.sergejava.telegram_app.repository.ProductRepository;
import com.sergejava.telegram_app.repository.UserRepository;
import com.sergejava.telegram_app.security.service.JwtService;
import com.sergejava.telegram_app.service.CategoryService;
import com.sergejava.telegram_app.service.ProductService;
import com.sergejava.telegram_app.service.UserService;
import com.sergejava.telegram_app.util.TestContainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CartItemControllerTest extends TestContainers {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private CategoryDTO categoryDTO;

    private ProductDTO productDTO;

    private User user;
    private String token;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        productRepository.deleteAll();
        categoryRepository.deleteAll();

        categoryDTO = CategoryDTO.builder()
                .name("Shoes")
                .build();

        categoryDTO = categoryService.createCategory(categoryDTO);
        CreateProductRequest productRequest = CreateProductRequest.builder()
                .name("TestProduct")
                .price(new BigDecimal(10000))
                .categoryId(categoryDTO.getId())
                .categoryName(categoryDTO.getName())
                .sizes(Map.of("M", 10))
                .imageUrls(List.of("mainURL", "testURL"))
                .build();
        productDTO = productService.createProduct(productRequest);
        user = User.builder()
                .userId(1111L)
                .firstName("Test")
                .username("test")
                .allowsWriteToPM(true)
                .languageCode("EN")
                .roles(Set.of(new Role(32,"USER")))
                .build();
        token = jwtService.generateJWT(user);
        UserDTO userDTO = UserMapper.toDto(user);
        user = UserMapper.toEntity(userService.saveUser(userDTO));
    }

    @Test
    @DisplayName("Добавление товара в корзину: Success")
    void addItemToCart_Success() throws Exception {
        AddItemToCartRequest addItemToCartRequest = AddItemToCartRequest.builder()
                .productId(productDTO.getId())
                .productSize("M")
                .quantity(2)
                .build();
        mockMvc.perform(post("/api/cartItems")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(addItemToCartRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product_name").value(productDTO.getName()))
                .andExpect(jsonPath("$.quantity").value(addItemToCartRequest.getQuantity()))
                .andExpect(jsonPath("$.product_id").value(productDTO.getId()))
                .andExpect(jsonPath("$.main_image_url").value("mainURL"))
                .andExpect(jsonPath("$.price").value(10000));

        mockMvc.perform(post("/api/cartItems")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(addItemToCartRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product_name").value(productDTO.getName()))
                .andExpect(jsonPath("$.quantity").value(4))
                .andExpect(jsonPath("$.product_id").value(productDTO.getId()))
                .andExpect(jsonPath("$.main_image_url").value("mainURL"))
                .andExpect(jsonPath("$.price").value(10000));
    }

    @Test
    @DisplayName("Удаление товара из корзины: Success")
    void deleteItemById_Success() throws Exception {
        AddItemToCartRequest addItemToCartRequest = AddItemToCartRequest.builder()
                .productId(productDTO.getId())
                .productSize("M")
                .quantity(2)
                .build();
        String cartDtoStr = mockMvc.perform(post("/api/cartItems")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(addItemToCartRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/cart")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_telegram_id").value(user.getUserId()))
                .andExpect(jsonPath("$.cart_items").isNotEmpty());

        CartDTO cartDTO = objectMapper.readValue(cartDtoStr, CartDTO.class);
        mockMvc.perform(delete("/api/cartItems/" + cartDTO.getId())
                .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk());
        mockMvc.perform(get("/api/cart")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cart_items").isEmpty());
    }

    @Test
    @DisplayName("Пользователь пытается удалить товар из корзины, которая ему не принадлежит: Failure")
    void deleteItemById_Failure() throws Exception {
        User anotherUser = User.builder()
                .userId(12356L)
                .firstName("anotherUser")
                .username("anotherTest")
                .allowsWriteToPM(true)
                .languageCode("EN")
                .roles(Set.of(new Role(32,"USER")))
                .build();
        String anotherToken = jwtService.generateJWT(anotherUser);
        UserDTO userDTO = UserMapper.toDto(anotherUser);
        UserMapper.toEntity(userService.saveUser(userDTO));

        AddItemToCartRequest addItemToCartRequest = AddItemToCartRequest.builder()
                .productId(productDTO.getId())
                .productSize("M")
                .quantity(2)
                .build();
        String cartDtoStr = mockMvc.perform(post("/api/cartItems")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(addItemToCartRequest)))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        mockMvc.perform(MockMvcRequestBuilders.get("/api/cart")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_telegram_id").value(user.getUserId()))
                .andExpect(jsonPath("$.cart_items").isNotEmpty());

        CartDTO cartDTO = objectMapper.readValue(cartDtoStr, CartDTO.class);
        mockMvc.perform(delete("/api/cartItems/" + cartDTO.getId())
                        .header("Authorization", "Bearer " + anotherToken))
                .andExpect(status().is(403))
                .andExpect(jsonPath("$").value("CartItem is in a cart that doesn't belong to you!"));
    }

    @Test
    @DisplayName("Добавление товара в корзину: Failure, UserNotFoundException")
    void addItemToCart_Failure_UserNotFound() throws Exception {
        User anotherUser = User.builder()
                .userId(12356L)
                .firstName("anotherUser")
                .username("anotherTest")
                .allowsWriteToPM(true)
                .languageCode("EN")
                .roles(Set.of(new Role(32,"USER")))
                .build();
        String anotherToken = jwtService.generateJWT(anotherUser);
        AddItemToCartRequest addItemToCartRequest = AddItemToCartRequest.builder()
                .productId(productDTO.getId())
                .productSize("M")
                .quantity(2)
                .build();
        mockMvc.perform(post("/api/cartItems")
                        .header("Authorization", "Bearer " + anotherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(addItemToCartRequest)))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$").value("User with 'user_id' " + anotherUser.getUserId() + " doesn't exist! Please register in the system."));
    }

}