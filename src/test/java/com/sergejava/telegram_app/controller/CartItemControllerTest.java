package com.sergejava.telegram_app.controller;

import com.sergejava.telegram_app.dto.AddItemToCartRequest;
import com.sergejava.telegram_app.dto.CartItemDTO;
import com.sergejava.telegram_app.dto.CategoryDTO;
import com.sergejava.telegram_app.dto.ChangeItemQuantityRequest;
import com.sergejava.telegram_app.dto.CreateProductRequest;
import com.sergejava.telegram_app.dto.ProductDTO;
import com.sergejava.telegram_app.dto.UserDTO;
import com.sergejava.telegram_app.entity.CartItem;
import com.sergejava.telegram_app.entity.Product;
import com.sergejava.telegram_app.entity.ProductSize;
import com.sergejava.telegram_app.entity.Role;
import com.sergejava.telegram_app.entity.User;
import com.sergejava.telegram_app.mapper.UserMapper;
import com.sergejava.telegram_app.repository.CartItemRepository;
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
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
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
    private CartItemRepository cartItemRepository;

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
                .sizes(Map.of("M", 10, "S", 2))
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
    void addItemToCart_Failure_InsufficientStock() throws Exception {
        CreateProductRequest productRequest = CreateProductRequest.builder()
                .name("Test")
                .price(new BigDecimal(10000))
                .categoryId(categoryDTO.getId())
                .sizes(Map.of("M", 0))
                .imageUrls(List.of("mainURL", "testURL"))
                .build();
        productDTO = productService.createProduct(productRequest);
        AddItemToCartRequest addItemToCartRequest = AddItemToCartRequest.builder()
                .productId(productDTO.getId())
                .productSize("M")
                .quantity(2)
                .build();
        mockMvc.perform(post("/api/cartItems")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(addItemToCartRequest)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$").value("The total stock of the product is zero."));

        productRequest = CreateProductRequest.builder()
                .name("Test")
                .price(new BigDecimal(10000))
                .categoryId(categoryDTO.getId())
                .sizes(Map.of("M", 10, "S", 0))
                .imageUrls(List.of("mainURL", "testURL"))
                .build();
        productDTO = productService.createProduct(productRequest);
        addItemToCartRequest = AddItemToCartRequest.builder()
                .productId(productDTO.getId())
                .productSize("S")
                .quantity(2)
                .build();
        mockMvc.perform(post("/api/cartItems")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(addItemToCartRequest)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$").value("The stock of the required product size is 0."));
        addItemToCartRequest = AddItemToCartRequest.builder()
                .productId(productDTO.getId())
                .productSize("M")
                .quantity(12)
                .build();
        mockMvc.perform(post("/api/cartItems")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(addItemToCartRequest)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$").value("The quantity of the requested product is greater than the stock."));

        productRequest = CreateProductRequest.builder()
                .name("Test")
                .price(new BigDecimal(10000))
                .categoryId(categoryDTO.getId())
                .sizes(Map.of("M", 10, "S", 1))
                .imageUrls(List.of("mainURL", "testURL"))
                .build();
        productDTO = productService.createProduct(productRequest);
        addItemToCartRequest = AddItemToCartRequest.builder()
                .productId(productDTO.getId())
                .productSize("S")
                .quantity(2)
                .build();
        mockMvc.perform(post("/api/cartItems")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(addItemToCartRequest)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$").value("The requested quantity is greater than the stock size!"));
    }

    @Test
    @DisplayName("Удаление товара из корзины: Success")
    void deleteItemById_Success() throws Exception {
        AddItemToCartRequest addItemToCartRequest = AddItemToCartRequest.builder()
                .productId(productDTO.getId())
                .productSize("M")
                .quantity(2)
                .build();
        String cartItemDtoStr = mockMvc.perform(post("/api/cartItems")
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

        CartItemDTO cartItemDTO = objectMapper.readValue(cartItemDtoStr, CartItemDTO.class);
        mockMvc.perform(delete("/api/cartItems/" + cartItemDTO.getId())
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
        String cartItemDTOStr = mockMvc.perform(post("/api/cartItems")
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

        CartItemDTO cartItemDTO = objectMapper.readValue(cartItemDTOStr, CartItemDTO.class);
        mockMvc.perform(delete("/api/cartItems/" + cartItemDTO.getId())
                        .header("Authorization", "Bearer " + anotherToken))
                .andExpect(status().is(403))
                .andExpect(jsonPath("$").value("CartItem is in a cart that doesn't belong to you!"));
    }

    @Test
    @DisplayName("Добавление товара в корзину: UserNotFoundException")
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

    @Test
    @DisplayName("Уменьшение количества CartItem в корзине: Success")
    void reduceItemQuantity_Success() throws Exception {
        AddItemToCartRequest addItemToCartRequest = AddItemToCartRequest.builder()
                .productId(productDTO.getId())
                .productSize("M")
                .quantity(5)
                .build();
        String cartItemDTOStr = mockMvc.perform(post("/api/cartItems")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(addItemToCartRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(5))
                .andReturn().getResponse().getContentAsString();
        Optional<Product> optionalProduct = productRepository.findProduct(productDTO.getId());
        assertThat(optionalProduct).isNotEmpty();
        Product product = optionalProduct.get();
        Optional<ProductSize> optionalProductSize = product.getProductSizes().stream()
                .filter(prSize -> prSize.getSize().getName().equals("M"))
                .findFirst();
        assertThat(optionalProductSize).isNotEmpty();
        ProductSize productSize = optionalProductSize.get();
        assertEquals(7, optionalProduct.get().getStock());
        assertEquals(5, productSize.getStock());

        CartItemDTO cartItemDTO = objectMapper.readValue(cartItemDTOStr, CartItemDTO.class);
        ChangeItemQuantityRequest reduceItemRequest = new ChangeItemQuantityRequest(cartItemDTO.getId(), 3);
        mockMvc.perform(patch("/api/cartItems/reduce")
                .header("Authorization", "Bearer " + token)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(reduceItemRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(2));
        optionalProduct = productRepository.findProduct(productDTO.getId());
        assertThat(optionalProduct).isNotEmpty();
        product = optionalProduct.get();
        optionalProductSize = product.getProductSizes().stream()
                .filter(prSize -> prSize.getSize().getName().equals("M"))
                .findFirst();
        assertThat(optionalProductSize).isNotEmpty();
        productSize = optionalProductSize.get();
        assertEquals(10, product.getStock());
        assertEquals(8, productSize.getStock());
    }

    @Test
    @DisplayName("CartItem должен удалиться, когда его quantity = 1 или меньше quantity из запроса.")
    void reduceItemQuantity_ShouldDeleteCartItem_Success() throws Exception {
        AddItemToCartRequest addItemToCartRequest = AddItemToCartRequest.builder()
                .productId(productDTO.getId())
                .productSize("M")
                .quantity(5)
                .build();
        String cartItemDTOStr = mockMvc.perform(post("/api/cartItems")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(addItemToCartRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(5))
                .andReturn().getResponse().getContentAsString();
        CartItemDTO cartItemDTO = objectMapper.readValue(cartItemDTOStr, CartItemDTO.class);
        Optional<CartItem> optionalCartItem = cartItemRepository.findCartItemById(cartItemDTO.getId());
        assertThat(optionalCartItem).isNotEmpty();
        Optional<Product> optionalProduct = productRepository.findProduct(productDTO.getId());
        assertThat(optionalProduct).isNotEmpty();
        Product product = optionalProduct.get();
        assertEquals(7, product.getStock());

        ChangeItemQuantityRequest reduceItemRequest = new ChangeItemQuantityRequest(cartItemDTO.getId(), 5);
        mockMvc.perform(patch("/api/cartItems/reduce")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(reduceItemRequest)))
                .andExpect(status().is(204))
                .andExpect(jsonPath("$").value("CartItem with ID '" + cartItemDTO.getId() + "' has been removed!"));
        optionalCartItem = cartItemRepository.findCartItemById(cartItemDTO.getId());
        assertThat(optionalCartItem).isEmpty();

        optionalProduct = productRepository.findProduct(productDTO.getId());
        assertThat(optionalProduct).isNotEmpty();
        product = optionalProduct.get();
        assertEquals(12, product.getStock());

        cartItemDTOStr = mockMvc.perform(post("/api/cartItems")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(addItemToCartRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(5))
                .andReturn().getResponse().getContentAsString();
        cartItemDTO = objectMapper.readValue(cartItemDTOStr, CartItemDTO.class);
        reduceItemRequest = new ChangeItemQuantityRequest(cartItemDTO.getId(), 4);
        mockMvc.perform(patch("/api/cartItems/reduce")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(reduceItemRequest)))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.quantity").value(1))
                .andExpect(jsonPath("$.id").value(cartItemDTO.getId()));
        mockMvc.perform(patch("/api/cartItems/reduce")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(reduceItemRequest)))
                .andExpect(status().is(204))
                .andExpect(jsonPath("$").value("CartItem with ID '" + cartItemDTO.getId() + "' has been removed!"));
    }

    @Test
    @DisplayName("Попытка уменьшить несуществующий CartItem приводит к CartItemNotFoundException")
    void reduceItemQuantity_Failure_CartItemNotFound() throws Exception {
        ChangeItemQuantityRequest reduceItemRequest = new ChangeItemQuantityRequest(9999L, 5);
        mockMvc.perform(patch("/api/cartItems/reduce")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(reduceItemRequest)))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$")
                        .value("CartItem with ID '"+ reduceItemRequest.getItemId() +"' not found!"));
    }

    @Test
    @DisplayName("Уменьшение CartItem, который находиться в корзине непринадлежащей пользователю, приводит к исключению.")
    void reduceItemQuantity_Failure_CartOwnership() throws Exception {
        AddItemToCartRequest addItemToCartRequest = AddItemToCartRequest.builder()
                .productId(productDTO.getId())
                .productSize("M")
                .quantity(5)
                .build();
        String cartItemDTOStr = mockMvc.perform(post("/api/cartItems")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(addItemToCartRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(5))
                .andReturn().getResponse().getContentAsString();
        CartItemDTO cartItemDTO = objectMapper.readValue(cartItemDTOStr, CartItemDTO.class);

        User anotherUser = User.builder()
                .userId(12356L)
                .firstName("anotherUser")
                .username("anotherTest")
                .allowsWriteToPM(true)
                .languageCode("EN")
                .roles(Set.of(new Role(32, "USER")))
                .build();
        String anotherToken = jwtService.generateJWT(anotherUser);
        ChangeItemQuantityRequest reduceItemRequest = new ChangeItemQuantityRequest(cartItemDTO.getId(), 5);
        mockMvc.perform(patch("/api/cartItems/reduce")
                        .header("Authorization", "Bearer " + anotherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(reduceItemRequest)))
                .andExpect(status().is(403))
                .andExpect(jsonPath("$")
                        .value("The CartItem you are trying to change is in a cart that doesn't belong to you!"));
        Optional<CartItem> optionalCartItem = cartItemRepository.findCartItemById(cartItemDTO.getId());
        assertThat(optionalCartItem).isNotEmpty();
        assertEquals(5, cartItemDTO.getQuantity());
    }

    @Test
    @DisplayName("Увеличение CartItem, который находиться в корзине непринадлежащей пользователю, приводит к исключению.")
    void increaseItemQuantity_Failure_CartOwnership() throws Exception {
        AddItemToCartRequest addItemToCartRequest = AddItemToCartRequest.builder()
                .productId(productDTO.getId())
                .productSize("M")
                .quantity(5)
                .build();
        String cartItemDTOStr = mockMvc.perform(post("/api/cartItems")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(addItemToCartRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(5))
                .andReturn().getResponse().getContentAsString();
        CartItemDTO cartItemDTO = objectMapper.readValue(cartItemDTOStr, CartItemDTO.class);

        User anotherUser = User.builder()
                .userId(12356L)
                .firstName("anotherUser")
                .username("anotherTest")
                .allowsWriteToPM(true)
                .languageCode("EN")
                .roles(Set.of(new Role(32, "USER")))
                .build();
        String anotherToken = jwtService.generateJWT(anotherUser);
        ChangeItemQuantityRequest increaseItemRequest = new ChangeItemQuantityRequest(cartItemDTO.getId(), 5);
        mockMvc.perform(patch("/api/cartItems/increase")
                        .header("Authorization", "Bearer " + anotherToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(increaseItemRequest)))
                .andExpect(status().is(403))
                .andExpect(jsonPath("$")
                        .value("The CartItem you are trying to change is in a cart that doesn't belong to you!"));
        Optional<CartItem> optionalCartItem = cartItemRepository.findCartItemById(cartItemDTO.getId());
        assertThat(optionalCartItem).isNotEmpty();
        assertEquals(5, cartItemDTO.getQuantity());
    }

    @Test
    @DisplayName("Попытка увеличить несуществующий CartItem приводит к CartItemNotFoundException")
    void increaseItemQuantity_Failure_CartItemNotFound() throws Exception {
        ChangeItemQuantityRequest increaseItemRequest = new ChangeItemQuantityRequest(9999L, 5);
        mockMvc.perform(patch("/api/cartItems/increase")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(increaseItemRequest)))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$")
                        .value("CartItem with ID '"+ increaseItemRequest.getItemId() +"' not found!"));
    }

    @Test
    @DisplayName("Успешное увеличение CartItem.")
    void increaseItemQuantity_Success() throws Exception {
        AddItemToCartRequest addItemToCartRequest = AddItemToCartRequest.builder()
                .productId(productDTO.getId())
                .productSize("M")
                .quantity(5)
                .build();
        String cartItemDTOStr = mockMvc.perform(post("/api/cartItems")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(addItemToCartRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(5))
                .andReturn().getResponse().getContentAsString();
        CartItemDTO cartItemDTO = objectMapper.readValue(cartItemDTOStr, CartItemDTO.class);
        ChangeItemQuantityRequest increaseItemRequest = new ChangeItemQuantityRequest(cartItemDTO.getId(), 4);
        int expectedQuantity = 9;
        mockMvc.perform(patch("/api/cartItems/increase")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(increaseItemRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(cartItemDTO.getId()))
                .andExpect(jsonPath("$.quantity").value(expectedQuantity));
        Optional<Product> optionalProduct = productRepository.findProduct(productDTO.getId());
        assertThat(optionalProduct).isNotEmpty();
        assertEquals(productDTO.getStock() - expectedQuantity, optionalProduct.get().getStock());
    }

    @Test
    @DisplayName("Увеличение quantity у CartItem: Failure")
    void increaseItemQuantity_Failure() throws Exception {
        //size M
        AddItemToCartRequest addItemToCartRequest = AddItemToCartRequest.builder()
                .productId(productDTO.getId())
                .productSize("M")
                .quantity(5)
                .build();
        String cartItemDTOStrM = mockMvc.perform(post("/api/cartItems")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(addItemToCartRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(5))
                .andReturn().getResponse().getContentAsString();
        CartItemDTO cartItemDTOSizeM = objectMapper.readValue(cartItemDTOStrM, CartItemDTO.class);
        ChangeItemQuantityRequest increaseItemRequest = new ChangeItemQuantityRequest(cartItemDTOSizeM.getId(), productDTO.getStock() + 10);
        mockMvc.perform(patch("/api/cartItems/increase")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(increaseItemRequest)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$").value("The quantity of the requested product is greater than the stock."));
        //size S
        addItemToCartRequest = AddItemToCartRequest.builder()
                .productId(productDTO.getId())
                .productSize("S")
                .quantity(1)
                .build();
        String cartItemDTOStrS = mockMvc.perform(post("/api/cartItems")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(addItemToCartRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(1))
                .andReturn().getResponse().getContentAsString();
        CartItemDTO cartItemDTOSizeS = objectMapper.readValue(cartItemDTOStrS, CartItemDTO.class);
        increaseItemRequest = new ChangeItemQuantityRequest(cartItemDTOSizeS.getId(), 5);
        mockMvc.perform(patch("/api/cartItems/increase")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(increaseItemRequest)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$").value("The requested quantity is greater than the stock size!"));
        increaseItemRequest = new ChangeItemQuantityRequest(cartItemDTOSizeS.getId(), 1);
        mockMvc.perform(patch("/api/cartItems/increase")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(increaseItemRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(2));
        mockMvc.perform(patch("/api/cartItems/increase")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(increaseItemRequest)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$").value("The stock of the required product size is 0."));
        //
        increaseItemRequest = new ChangeItemQuantityRequest(cartItemDTOSizeM.getId(), 5);
        mockMvc.perform(patch("/api/cartItems/increase")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(increaseItemRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.quantity").value(10));
        mockMvc.perform(patch("/api/cartItems/increase")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(increaseItemRequest)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$").value("the stock of the product is 0."));
        Product product = productRepository.findProduct(productDTO.getId()).get();
        assertEquals(0, product.getStock());
    }

}