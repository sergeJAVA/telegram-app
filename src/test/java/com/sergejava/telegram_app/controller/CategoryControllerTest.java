package com.sergejava.telegram_app.controller;

import com.sergejava.telegram_app.dto.CategoryDTO;
import com.sergejava.telegram_app.dto.UserDTO;
import com.sergejava.telegram_app.entity.Role;
import com.sergejava.telegram_app.entity.User;
import com.sergejava.telegram_app.mapper.UserMapper;
import com.sergejava.telegram_app.repository.CategoryRepository;
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
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.Set;

import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class CategoryControllerTest extends TestContainers {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private User admin;
    private String adminToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        categoryRepository.deleteAll();

        admin = User.builder()
                .userId(1111L)
                .firstName("Test")
                .username("test")
                .allowsWriteToPM(true)
                .languageCode("EN")
                .roles(Set.of(new Role(32,"ADMIN")))
                .build();
        adminToken = jwtService.generateJWT(admin);
        UserDTO userDTO = UserMapper.toDto(admin);
        admin = UserMapper.toEntity(userService.saveUser(userDTO));
    }

    @Test
    @DisplayName("Успешное создание категории.")
    void createCategory_Success() throws Exception {
        CategoryDTO request = CategoryDTO.builder()
                .name("Jacket")
                .description("test description")
                .build();
        mockMvc.perform(post("/api/categories")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(request.getName()))
                .andExpect(jsonPath("$.description").value(request.getDescription()));
    }

    @Test
    @DisplayName("Без роли ADMIN доступ отклонён.")
    void createCategory_Failure_AccessDenied() throws Exception {
        User user = User.builder()
                .userId(1212L)
                .firstName("User")
                .username("test")
                .allowsWriteToPM(true)
                .languageCode("EN")
                .roles(Set.of(new Role(10,"USER")))
                .build();
        String userToken = jwtService.generateJWT(user);
        CategoryDTO request = CategoryDTO.builder()
                .name("Jacket")
                .description("test description")
                .build();
        mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().is(401))
                .andExpect(jsonPath("$.message").value("Access Denied"));
    }

    @Test
    @DisplayName("Получение всех категорий")
    void findAll() throws Exception {
        mockMvc.perform(get("/api/categories")
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
        User user = User.builder()
                .userId(1212L)
                .firstName("User")
                .username("test")
                .allowsWriteToPM(true)
                .languageCode("EN")
                .roles(Set.of(new Role(10,"USER")))
                .build();
        String userToken = jwtService.generateJWT(user);
        CategoryDTO request = CategoryDTO.builder()
                .name("Jacket")
                .description("test description")
                .build();
        mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.name").value(request.getName()));
        request = CategoryDTO.builder()
                .name("Boots")
                .description("test description")
                .build();
        mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.name").value(request.getName()));

        mockMvc.perform(get("/api/categories")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[*].name", containsInAnyOrder("Jacket", "Boots")));
    }

    @Test
    void createCategory_Failure_AlreadyExists() throws Exception {
        CategoryDTO request = CategoryDTO.builder()
                .name("Jacket")
                .description("test description")
                .build();
        mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.name").value(request.getName()));
        mockMvc.perform(post("/api/categories")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().is(201))
                .andExpect(jsonPath("$")
                        .value(String.format("Category with name '%s' already exists!", request.getName())));
    }

}