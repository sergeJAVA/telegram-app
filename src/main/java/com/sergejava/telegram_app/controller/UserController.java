package com.sergejava.telegram_app.controller;

import com.sergejava.telegram_app.dto.AddRoleRequest;
import com.sergejava.telegram_app.dto.UserDTO;
import com.sergejava.telegram_app.security.TokenAuthentication;
import com.sergejava.telegram_app.security.TokenData;
import com.sergejava.telegram_app.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @GetMapping("/validate-presence")
    public ResponseEntity<?> validatePresence(Authentication authentication) {
        TokenAuthentication tokenAuthentication = (TokenAuthentication) authentication;
        TokenData tokenData = tokenAuthentication.getTokenData();
        boolean isPresent = userService.validatePresence(tokenData);
        if (isPresent) {
            return ResponseEntity.ok(true);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
    }

    @PostMapping("/addRole")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> addRole(@RequestBody @Valid AddRoleRequest request) {
        UserDTO userDTO = userService.addRole(request.getUserId(), request.getRoleName());
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/becomeAdmin")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<?> becomeAdmin(Authentication authentication) {
        TokenAuthentication tokenAuthentication = (TokenAuthentication) authentication;
        TokenData tokenData = tokenAuthentication.getTokenData();
        UserDTO response = userService.becomeAdmin(tokenData);
        return ResponseEntity.ok(response);
    }

}
