package com.sergejava.telegram_app.controller;

import com.sergejava.telegram_app.config.AppConfig;
import com.sergejava.telegram_app.dto.AddRoleRequest;
import com.sergejava.telegram_app.dto.UserDTO;
import com.sergejava.telegram_app.entity.Role;
import com.sergejava.telegram_app.entity.User;
import com.sergejava.telegram_app.mapper.UserMapper;
import com.sergejava.telegram_app.repository.UserRepository;
import com.sergejava.telegram_app.security.service.JwtService;
import com.sergejava.telegram_app.service.UserService;
import com.sergejava.telegram_app.util.TestContainers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.cache.CacheManager;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerTest extends TestContainers {

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

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private AppConfig appConfig;

    private String token;

    private User admin;
    private User user;

    private String adminToken;
    private String userToken;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

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
                .userId(9999L)
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
    void validatePresence_Success() throws Exception {
        mockMvc.perform(get("/api/users/validate-presence")
                .header("Authorization", "Bearer " + userToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(true));
    }

    @Test
    void validatePresence_NotFound() throws Exception {
        User guest = User.builder()
                .userId(12312L)
                .firstName("Guest")
                .username("guest")
                .allowsWriteToPM(true)
                .languageCode("EN")
                .roles(Set.of(new Role(32,"GUEST")))
                .build();
        String guestToken = jwtService.generateJWT(guest);
        mockMvc.perform(get("/api/users/validate-presence")
                        .header("Authorization", "Bearer " + guestToken))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$").value(false));
    }

    @Test
    void addRole_Success() throws Exception {
        User user = userRepository.findByUserId(admin.getUserId()).get();
        assertThat(user).isNotNull();
        assertThat(user.getRoles()).hasSize(1);
        AddRoleRequest request = new AddRoleRequest(admin.getUserId(), "GUEST");
        mockMvc.perform(post("/api/users/addRole")
                .header("Authorization", "Bearer " + adminToken)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.roles[*].name", containsInAnyOrder("GUEST", "USER")));
        user = userRepository.findByUserId(admin.getUserId()).get();
        assertEquals(2, user.getRoles().size());
        assertThat(user.getRoles().stream()
                .filter(r -> r.getName()
                .equals("GUEST"))
                .findAny()
                .get()).isNotNull();
    }

    @Test
    void addRole_Failure_UserNotFound() throws Exception {
        Long userId = 999999L;
        AddRoleRequest request = new AddRoleRequest(userId, "GUEST");
        mockMvc.perform(post("/api/users/addRole")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$").value(String.format("User with 'user_id' %d doesn't exist!", userId)));
    }

    @Test
    void addRole_Failure_RoleNotFound() throws Exception {
        AddRoleRequest request = new AddRoleRequest(admin.getUserId(), "WRONG_ROLE");
        mockMvc.perform(post("/api/users/addRole")
                        .header("Authorization", "Bearer " + adminToken)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$").value(String.format("Role %s not found!", request.getRoleName())));
    }

    @Test
    void becomeAdmin() throws Exception {
        User userDB = userRepository.findByUserId(user.getUserId()).get();
        assertThat(userDB).isNotNull();
        assertThat(userDB.getRoles()).hasSize(1);
        assertThat(userDB.getRoles().stream().findFirst().get().getName()).isEqualTo("USER");

        mockMvc.perform(get("/api/users/becomeAdmin")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().is(403))
                .andExpect(jsonPath("$").value("The ADMIN is not specified in the system."));

        appConfig.setAdminId(user.getUserId());
        mockMvc.perform(get("/api/users/becomeAdmin")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().is(200))
                .andExpect(jsonPath("$.roles", hasSize(2)))
                .andExpect(jsonPath("$.roles[*].name", containsInAnyOrder("ADMIN", "USER")));
        userDB = userRepository.findByUserId(user.getUserId()).get();
        assertThat(userDB).isNotNull();
        assertThat(userDB.getRoles()).hasSize(2);
        List<String> roleNames = userDB.getRoles().stream().map(Role::getName).toList();
        assertTrue(roleNames.contains("USER"));
        assertTrue(roleNames.contains("ADMIN"));

        Long wrongAdminId = 7878787L;
        appConfig.setAdminId(wrongAdminId);
        mockMvc.perform(get("/api/users/becomeAdmin")
                        .header("Authorization", "Bearer " + userToken))
                .andExpect(status().is(400))
                .andExpect(jsonPath("$").value("The user does not meet the requirements"));

        User someUser = User.builder()
                .userId(12312L)
                .firstName("Guest")
                .username("guest")
                .allowsWriteToPM(true)
                .languageCode("EN")
                .roles(Set.of(new Role(44, "ADMIN")))
                .build();
        String someUserToken = jwtService.generateJWT(someUser);
        appConfig.setAdminId(someUser.getUserId());
        mockMvc.perform(get("/api/users/becomeAdmin")
                        .header("Authorization", "Bearer " + someUserToken))
                .andExpect(status().is(404))
                .andExpect(jsonPath("$")
                        .value(String.format("User with 'user_id' %d doesn't exist! Please register in the system.", someUser.getUserId())));
    }

}