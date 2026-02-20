package com.sergejava.telegram_app.controller;

import com.sergejava.telegram_app.dto.CategoryDTO;
import com.sergejava.telegram_app.dto.CreateProductRequest;
import com.sergejava.telegram_app.dto.ProductDTO;
import com.sergejava.telegram_app.dto.ProductImageDTO;
import com.sergejava.telegram_app.dto.UserDTO;
import com.sergejava.telegram_app.entity.Role;
import com.sergejava.telegram_app.entity.User;
import com.sergejava.telegram_app.mapper.UserMapper;
import com.sergejava.telegram_app.repository.CategoryRepository;
import com.sergejava.telegram_app.repository.OrderRepository;
import com.sergejava.telegram_app.repository.ProductRepository;
import com.sergejava.telegram_app.repository.UserRepository;
import com.sergejava.telegram_app.security.service.JwtService;
import com.sergejava.telegram_app.service.CategoryService;
import com.sergejava.telegram_app.service.ProductService;
import com.sergejava.telegram_app.service.UserService;
import com.sergejava.telegram_app.util.TestContainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ProductControllerTest extends TestContainers {

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

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    private User admin;
    private User user;

    private String adminToken;
    private String userToken;

    private CategoryDTO categoryDTO;

    private CreateProductRequest createRequest;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        categoryRepository.deleteAll();
        productRepository.deleteAll();

        categoryDTO = CategoryDTO.builder()
                .name("Shoes")
                .build();

        categoryDTO = categoryService.createCategory(categoryDTO);
        createRequest = CreateProductRequest.builder()
                .name("TestProduct")
                .price(new BigDecimal("10000.00"))
                .categoryId(categoryDTO.getId())
                .sizes(Map.of("M", 10, "S", 10))
                .imageUrls(List.of("mainURL", "testURL"))
                .build();
        admin = User.builder()
                .userId(1111L)
                .firstName("TestAdmin")
                .username("admin")
                .allowsWriteToPM(true)
                .languageCode("EN")
                .roles(Set.of(new Role(32,"ADMIN")))
                .build();
        adminToken = jwtService.generateJWT(admin);
        UserDTO adminDTO = UserMapper.toDto(admin);
        admin = UserMapper.toEntity(userService.saveUser(adminDTO));

        user = User.builder()
                .userId(2222L)
                .firstName("TestUser")
                .username("user")
                .allowsWriteToPM(true)
                .languageCode("EN")
                .roles(Set.of(new Role(32,"USER")))
                .build();
        userToken = jwtService.generateJWT(user);
        UserDTO userDTO = UserMapper.toDto(user);
        user = UserMapper.toEntity(userService.saveUser(userDTO));
    }

    @Test
    void createProduct_Success() throws Exception {
        String response = mockMvc.perform(post("/api/products")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(createRequest.getName()))
                .andExpect(jsonPath("$.price").value(10000))
                .andExpect(jsonPath("$.stock").value(20))
                .andExpect(jsonPath("$.image_urls[*].url", containsInAnyOrder("mainURL", "testURL")))
                .andReturn().getResponse().getContentAsString();
        ProductDTO productDTO = objectMapper.readValue(response, ProductDTO.class);
        ProductImageDTO imageDTO = productDTO.getImageUrls().stream()
                .filter(i -> i.getUrl().equals("mainURL"))
                        .findFirst().get();
        assertThat(imageDTO.getUrl()).isEqualTo("mainURL");
        assertThat(imageDTO.getIsMain()).isEqualTo(true);
    }

    @Test
    void createProduct_Failure_CategoryNotFound() throws Exception {
        createRequest.setCategoryId(650L);
        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(createRequest)))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$")
                        .value(String.format("Category with ID '%d' not found!", createRequest.getCategoryId())));
    }

    @Test
    void createProduct_Failure_InvalidImageURL() throws Exception {
        createRequest.setImageUrls(Collections.emptyList());
        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(createRequest)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$")
                        .value("The URLs for the images are empty or null. Please provide a link to them."));
        createRequest.setImageUrls(List.of(""));
        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(createRequest)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$")
                        .value("One of the image URLs is invalid!"));
    }

    @Test
    void createProduct_Failure_SizeNotFound() throws Exception {
        String wrongSizeName = "WrongSize";
        createRequest.setSizes(Map.of(wrongSizeName, 1));
        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(createRequest)))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$")
                        .value(String.format("Size with name '%s' not found!", wrongSizeName.toUpperCase(Locale.ROOT))));
    }

    @Test
    void getProductsByCategory() throws Exception {
        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(createRequest.getName()));
        mockMvc.perform(get("/api/products/category/" + categoryDTO.getName())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(createRequest)))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].category_name").value("Shoes"))
                .andExpect(jsonPath("$.content[0].name").value("TestProduct"));

        categoryDTO = CategoryDTO.builder()
                .name("Sneakers")
                .build();
        categoryDTO = categoryService.createCategory(categoryDTO);
        createRequest.setName("Black sneakers");
        createRequest.setCategoryId(categoryDTO.getId());
        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Black sneakers"));
        mockMvc.perform(get("/api/products/category/" + categoryDTO.getName())
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(createRequest)))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.content", hasSize(1)))
                .andExpect(jsonPath("$.content[0].category_name").value("Sneakers"))
                .andExpect(jsonPath("$.content[0].name").value("Black sneakers"));

        mockMvc.perform(get("/api/products/category/" + "wrongCategoryName")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(createRequest)))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.content").isEmpty());
    }

    @Test
    void getAll() throws Exception {
        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(createRequest.getName()));

        categoryDTO = CategoryDTO.builder()
                .name("Sneakers")
                .build();
        categoryDTO = categoryService.createCategory(categoryDTO);
        createRequest.setName("Black sneakers");
        createRequest.setCategoryId(categoryDTO.getId());
        mockMvc.perform(post("/api/products")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(createRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Black sneakers"));

        mockMvc.perform(get("/api/products")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content", hasSize(2)));
    }

}