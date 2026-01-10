package com.sergejava.telegram_app.security;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TokenData {

    private Long userId;
    private String username;
    private Set<? extends GrantedAuthority> authorities;
    private String token;

}
