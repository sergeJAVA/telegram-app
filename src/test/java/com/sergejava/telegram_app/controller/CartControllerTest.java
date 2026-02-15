package com.sergejava.telegram_app.controller;

import com.sergejava.telegram_app.dto.CartDTO;
import com.sergejava.telegram_app.dto.UserDTO;
import com.sergejava.telegram_app.entity.User;
import com.sergejava.telegram_app.mapper.UserMapper;
import com.sergejava.telegram_app.repository.UserRepository;
import com.sergejava.telegram_app.security.service.JwtService;
import com.sergejava.telegram_app.service.UserService;
import com.sergejava.telegram_app.util.TestContainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CartControllerTest extends TestContainers {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private UserRepository userRepository;

    private String token;

    private User user;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        user = User.builder()
                .userId(1111L)
                .firstName("Test")
                .username("test")
                .allowsWriteToPM(true)
                .languageCode("EN")
                .build();
        UserDTO userDTO = UserMapper.toDto(user);
        user = UserMapper.toEntity(userService.saveUser(userDTO));
        token = jwtService.generateJWT(user);
    }

    @Test
    @DisplayName("Получение корзины: Success")
    void getCart_Success() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/cart")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_telegram_id").value(user.getUserId()))
                .andExpect(jsonPath("$.cart_items", hasSize(0)));

        Cache.ValueWrapper valueWrapper = cacheManager.getCache("cart").get(user.getUserId());
        assert valueWrapper != null;
        CartDTO cartDTO = (CartDTO) valueWrapper.get();
        assertThat(cartDTO).isNotNull();
        assertThat(cartDTO.getUserTelegramId().equals(user.getUserId()));

        cacheManager.getCache("cart").evict(user.getUserId());
        mockMvc.perform(MockMvcRequestBuilders.get("/api/cart")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_telegram_id").value(user.getUserId()))
                .andExpect(jsonPath("$.cart_items", hasSize(0)));
    }

    @Test
    @DisplayName("Получение корзины: Failure")
    void getCart_Failure() throws Exception {
        User testUser = User.builder()
                .userId(1234L)
                .firstName("user")
                .username("user")
                .allowsWriteToPM(true)
                .languageCode("EN")
                .build();
        String testToken = jwtService.generateJWT(testUser);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/cart")
                        .header("Authorization", "Bearer " + testToken))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$").value("User with 'user_id' " + testUser.getUserId() +
                        " doesn't exist! Please register in the system."));
    }

}