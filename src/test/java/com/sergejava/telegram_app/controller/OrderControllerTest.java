package com.sergejava.telegram_app.controller;

import com.sergejava.telegram_app.dto.AddItemToCartRequest;
import com.sergejava.telegram_app.dto.CategoryDTO;
import com.sergejava.telegram_app.dto.CreateOrderRequest;
import com.sergejava.telegram_app.dto.CreateProductRequest;
import com.sergejava.telegram_app.dto.OrderChangeStatusRequest;
import com.sergejava.telegram_app.dto.OrderDTO;
import com.sergejava.telegram_app.dto.ProductDTO;
import com.sergejava.telegram_app.dto.SearchOrdersRequest;
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

import static org.hamcrest.Matchers.hasSize;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OrderControllerTest extends TestContainers {

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
    private ProductDTO productDTO;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
        categoryRepository.deleteAll();
        productRepository.deleteAll();
        orderRepository.deleteAll();

        categoryDTO = CategoryDTO.builder()
                .name("Shoes")
                .build();

        categoryDTO = categoryService.createCategory(categoryDTO);
        CreateProductRequest productRequest = CreateProductRequest.builder()
                .name("TestProduct")
                .price(new BigDecimal("10000.00"))
                .categoryId(categoryDTO.getId())
                .sizes(Map.of("M", 10, "S", 2))
                .imageUrls(List.of("mainURL", "testURL"))
                .build();
        productDTO = productService.createProduct(productRequest);

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
    @DisplayName("Успешное оформление заказа.")
    void createOrder_Success() throws Exception {
        AddItemToCartRequest addItemToCartRequest = AddItemToCartRequest.builder()
                .productId(productDTO.getId())
                .productSize("M")
                .quantity(2)
                .build();
        mockMvc.perform(post("/api/cartItems")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(addItemToCartRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product_name").value(productDTO.getName()));
        CreateOrderRequest createOrderRequest = CreateOrderRequest.builder()
                .phoneNumber("+79999990011")
                .deliveryAddress("Test address")
                .build();
        mockMvc.perform(post("/api/orders")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(createOrderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andExpect(jsonPath("$.total_price").value(20000))
                .andExpect(jsonPath("$.delivery_address").value(createOrderRequest.getDeliveryAddress()))
                .andExpect(jsonPath("$.phone_number").value(createOrderRequest.getPhoneNumber()))
                .andExpect(jsonPath("$.created_at").exists());
    }

    @Test
    @DisplayName("Корзина не найдена, невозможно оформить заказ.")
    void crateOrder_Failure_CartNotFound() throws Exception {
        CreateOrderRequest createOrderRequest = CreateOrderRequest.builder()
                .phoneNumber("+79999990011")
                .deliveryAddress("Test address")
                .build();
        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(createOrderRequest)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$")
                        .value(String.format("The Cart for the user with userId '%d' was not found. " +
                                "Most likely, the user is not registered.", user.getUserId())));
    }

    @Test
    @DisplayName("Пустая корзина, невозможно оформить заказ.")
    void crateOrder_Failure_EmptyCart() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/cart")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user_telegram_id").value(user.getUserId()))
                .andExpect(jsonPath("$.cart_items", hasSize(0)));

        CreateOrderRequest createOrderRequest = CreateOrderRequest.builder()
                .phoneNumber("+79999990011")
                .deliveryAddress("Test address")
                .build();
        mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(createOrderRequest)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$").value("Your cart is empty, you cannot place an order."));
    }

    @Test
    @DisplayName("Успешно отменить заказ.")
    void cancelOrder_Success() throws Exception {
        AddItemToCartRequest addItemToCartRequest = AddItemToCartRequest.builder()
                .productId(productDTO.getId())
                .productSize("M")
                .quantity(2)
                .build();
        mockMvc.perform(post("/api/cartItems")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(addItemToCartRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product_name").value(productDTO.getName()));

        CreateOrderRequest createOrderRequest = CreateOrderRequest.builder()
                .phoneNumber("+79999990011")
                .deliveryAddress("Test address")
                .build();
        OrderDTO orderDTO = objectMapper.readValue(mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(createOrderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn().getResponse().getContentAsString(), OrderDTO.class);

        mockMvc.perform(post("/api/orders/" + orderDTO.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"))
                .andExpect(jsonPath("$.id").value(orderDTO.getId()));
    }

    @Test
    @DisplayName("Нельзя отменить несуществующий заказ")
    void cancelOrder_Failure_OrderNotFound() throws Exception {
        Long orderId = 555L;
        mockMvc.perform(post("/api/orders/" + orderId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$").value(String.format("Order with ID '%d' not found!", orderId)));
    }

    @Test
    @DisplayName("Заказ уже отменён.")
    void cancelOrder_Failure_AlreadyCancelled() throws Exception {
        AddItemToCartRequest addItemToCartRequest = AddItemToCartRequest.builder()
                .productId(productDTO.getId())
                .productSize("M")
                .quantity(2)
                .build();
        mockMvc.perform(post("/api/cartItems")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(addItemToCartRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product_name").value(productDTO.getName()));

        CreateOrderRequest createOrderRequest = CreateOrderRequest.builder()
                .phoneNumber("+79999990011")
                .deliveryAddress("Test address")
                .build();
        OrderDTO orderDTO = objectMapper.readValue(mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(createOrderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn().getResponse().getContentAsString(), OrderDTO.class);

        mockMvc.perform(post("/api/orders/" + orderDTO.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"))
                .andExpect(jsonPath("$.id").value(orderDTO.getId()));

        mockMvc.perform(post("/api/orders/" + orderDTO.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$")
                        .value(String.format("Order with ID '%d' has already been cancelled.", orderDTO.getId())));
    }

    @Test
    @DisplayName("Статус заказа успешно изменён.")
    void changeOrderStatus_Success() throws Exception {
        AddItemToCartRequest addItemToCartRequest = AddItemToCartRequest.builder()
                .productId(productDTO.getId())
                .productSize("M")
                .quantity(2)
                .build();
        mockMvc.perform(post("/api/cartItems")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(addItemToCartRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product_name").value(productDTO.getName()));

        CreateOrderRequest createOrderRequest = CreateOrderRequest.builder()
                .phoneNumber("+79999990011")
                .deliveryAddress("Test address")
                .build();
        OrderDTO orderDTO = objectMapper.readValue(mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(createOrderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn().getResponse().getContentAsString(), OrderDTO.class);

        OrderChangeStatusRequest changeStatusRequest = OrderChangeStatusRequest.builder()
                .id(orderDTO.getId())
                .status("shipped")
                .build();
        mockMvc.perform(patch("/api/orders")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(changeStatusRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderDTO.getId()))
                .andExpect(jsonPath("$.status").value("SHIPPED"));

        changeStatusRequest.setStatus("DELIVERED");
        mockMvc.perform(patch("/api/orders")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(changeStatusRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderDTO.getId()))
                .andExpect(jsonPath("$.status").value(changeStatusRequest.getStatus()));

        changeStatusRequest.setStatus("PAID");
        mockMvc.perform(patch("/api/orders")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(changeStatusRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderDTO.getId()))
                .andExpect(jsonPath("$.status").value(changeStatusRequest.getStatus()));

        changeStatusRequest.setStatus("PENDING");
        mockMvc.perform(patch("/api/orders")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(changeStatusRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(orderDTO.getId()))
                .andExpect(jsonPath("$.status").value(changeStatusRequest.getStatus()));

        changeStatusRequest.setStatus("CANCELLED");
        mockMvc.perform(patch("/api/orders")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(changeStatusRequest)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$").value("Invalid order status." +
                        " You can't set the status to CANCELLED; there's a different endpoint for that.\n" +
                        "Valid values: PENDING, PAID, SHIPPED, DELIVERED."));
    }

    @Test
    @DisplayName("Не удалось изменить статус: заказ не был найден.")
    void changeOrderStatus_Failure_NotFound() throws Exception {
        Long orderId = 444L;
        OrderChangeStatusRequest changeStatusRequest = OrderChangeStatusRequest.builder()
                .id(orderId)
                .status("shipped")
                .build();
        mockMvc.perform(patch("/api/orders")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(changeStatusRequest)))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$").value(String.format("Order with ID '%d' not found!", orderId)));
    }

    @Test
    @DisplayName("Не удалось изменить статус: заказ уже отменён.")
    void changeOrderStatus_Failure_AlreadyCancelled() throws Exception {
        AddItemToCartRequest addItemToCartRequest = AddItemToCartRequest.builder()
                .productId(productDTO.getId())
                .productSize("M")
                .quantity(2)
                .build();
        mockMvc.perform(post("/api/cartItems")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(addItemToCartRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product_name").value(productDTO.getName()));

        CreateOrderRequest createOrderRequest = CreateOrderRequest.builder()
                .phoneNumber("+79999990011")
                .deliveryAddress("Test address")
                .build();
        OrderDTO orderDTO = objectMapper.readValue(mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(createOrderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn().getResponse().getContentAsString(), OrderDTO.class);
        mockMvc.perform(post("/api/orders/" + orderDTO.getId())
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"))
                .andExpect(jsonPath("$.id").value(orderDTO.getId()));

        OrderChangeStatusRequest changeStatusRequest = OrderChangeStatusRequest.builder()
                .id(orderDTO.getId())
                .status("CANCELLED")
                .build();
        mockMvc.perform(patch("/api/orders")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(changeStatusRequest)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$").value(String.format("The order with ID '%d' has already been canceled." +
                        " You cannot change its status.", orderDTO.getId())));
    }

    @Test
    @DisplayName("Не удалось изменить статус: неправильный статус.")
    void changeOrderStatus_Failure_WrongStatus() throws Exception {
        AddItemToCartRequest addItemToCartRequest = AddItemToCartRequest.builder()
                .productId(productDTO.getId())
                .productSize("M")
                .quantity(2)
                .build();
        mockMvc.perform(post("/api/cartItems")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(addItemToCartRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product_name").value(productDTO.getName()));

        CreateOrderRequest createOrderRequest = CreateOrderRequest.builder()
                .phoneNumber("+79999990011")
                .deliveryAddress("Test address")
                .build();
        OrderDTO orderDTO = objectMapper.readValue(mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(createOrderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn().getResponse().getContentAsString(), OrderDTO.class);

        OrderChangeStatusRequest changeStatusRequest = OrderChangeStatusRequest.builder()
                .id(orderDTO.getId())
                .status("Wrong Status")
                .build();
        mockMvc.perform(patch("/api/orders")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(changeStatusRequest)))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$").value("Invalid order status." +
                        " Valid values: PENDING, PAID, SHIPPED, DELIVERED."));
    }

    @Test
    @DisplayName("Заказ пользователя успешно отменён.")
    void cancelMyOrder_Success() throws Exception {
        AddItemToCartRequest addItemToCartRequest = AddItemToCartRequest.builder()
                .productId(productDTO.getId())
                .productSize("M")
                .quantity(2)
                .build();
        mockMvc.perform(post("/api/cartItems")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(addItemToCartRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product_name").value(productDTO.getName()));

        CreateOrderRequest createOrderRequest = CreateOrderRequest.builder()
                .phoneNumber("+79999990011")
                .deliveryAddress("Test address")
                .build();
        OrderDTO orderDTO = objectMapper.readValue(mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(createOrderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn().getResponse().getContentAsString(), OrderDTO.class);

        mockMvc.perform(post("/api/orders/my/" + orderDTO.getId())
                .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"))
                .andExpect(jsonPath("$.id").value(orderDTO.getId()));
    }

    @Test
    @DisplayName("Заказ с таким ID не был найден.")
    void cancelMyOrder_Failure_OrderNotFound() throws Exception {
        Long orderId = 999L;
        mockMvc.perform(post("/api/orders/my/" + orderId)
                        .header("Authorization", "Bearer " + adminToken))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$").value(String.format("Order with ID '%d' not found!", orderId)));
    }

    @Test
    @DisplayName("Заказ не принадлежит пользователю.")
    void cancelMyOrder_Failure_OwnershipException() throws Exception {
        AddItemToCartRequest addItemToCartRequest = AddItemToCartRequest.builder()
                .productId(productDTO.getId())
                .productSize("M")
                .quantity(2)
                .build();
        mockMvc.perform(post("/api/cartItems")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(addItemToCartRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product_name").value(productDTO.getName()));

        CreateOrderRequest createOrderRequest = CreateOrderRequest.builder()
                .phoneNumber("+79999990011")
                .deliveryAddress("Test address")
                .build();
        OrderDTO orderDTO = objectMapper.readValue(mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(createOrderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn().getResponse().getContentAsString(), OrderDTO.class);

        mockMvc.perform(post("/api/orders/my/" + orderDTO.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$").value("The order does not belong to the user."));
    }

    @Test
    @DisplayName("Заказ пользователя уже отменён.")
    void cancelMyOrder_Failure_AlreadyCancelled() throws Exception {
        AddItemToCartRequest addItemToCartRequest = AddItemToCartRequest.builder()
                .productId(productDTO.getId())
                .productSize("M")
                .quantity(2)
                .build();
        mockMvc.perform(post("/api/cartItems")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(addItemToCartRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product_name").value(productDTO.getName()));

        CreateOrderRequest createOrderRequest = CreateOrderRequest.builder()
                .phoneNumber("+79999990011")
                .deliveryAddress("Test address")
                .build();
        OrderDTO orderDTO = objectMapper.readValue(mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(createOrderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn().getResponse().getContentAsString(), OrderDTO.class);

        mockMvc.perform(post("/api/orders/my/" + orderDTO.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("CANCELLED"))
                .andExpect(jsonPath("$.id").value(orderDTO.getId()));

        mockMvc.perform(post("/api/orders/my/" + orderDTO.getId())
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$")
                        .value( String.format("Order with ID '%d' has already been cancelled.", orderDTO.getId())));
    }

    @Test
    @DisplayName("Успешное получение заказов")
    void getMyOrders_Success() throws Exception {
        AddItemToCartRequest addItemToCartRequest = AddItemToCartRequest.builder()
                .productId(productDTO.getId())
                .productSize("M")
                .quantity(2)
                .build();
        mockMvc.perform(post("/api/cartItems")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(addItemToCartRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product_name").value(productDTO.getName()));

        CreateOrderRequest createOrderRequest = CreateOrderRequest.builder()
                .phoneNumber("+79999990011")
                .deliveryAddress("Test address")
                .build();
        OrderDTO orderDTO = objectMapper.readValue(mockMvc.perform(post("/api/orders")
                        .header("Authorization", "Bearer " + userToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(createOrderRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("PENDING"))
                .andReturn().getResponse().getContentAsString(), OrderDTO.class);

        SearchOrdersRequest searchOrdersRequest = SearchOrdersRequest.builder()
                .status("pending")
                .page(0)
                .size(5)
                .build();
        mockMvc.perform(post("/api/orders/my")
                .header("Authorization", "Bearer " + userToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(searchOrdersRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].id").value(orderDTO.getId()))
                .andExpect(jsonPath("$.content[0].total_price").value(20000))
                .andExpect(jsonPath("$.content[0].phone_number").value(createOrderRequest.getPhoneNumber()))
                .andExpect(jsonPath("$.content[0].delivery_address").value(createOrderRequest.getDeliveryAddress()))
                .andExpect(jsonPath("$.content[0].order_items").isNotEmpty())
                .andExpect(jsonPath("$.content[0].order_items", hasSize(1)))
                .andExpect(jsonPath("$.numberOfElements").value(1));;

        mockMvc.perform(post("/api/orders/my")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(searchOrdersRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isEmpty())
                .andExpect(jsonPath("$.numberOfElements").value(0));
    }

}