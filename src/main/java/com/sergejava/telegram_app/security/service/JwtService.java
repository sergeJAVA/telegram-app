package com.sergejava.telegram_app.security.service;

import com.sergejava.telegram_app.dto.InitDataUser;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

@Service
@Slf4j
public class JwtService {

    @Value("${jwt.secret}")
    private String secretKey;

    @Value("${jwt.life-time}")
    private Long lifeTime;

    public Long getUserIdFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("user_id", Long.class));
    }

    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("username", String.class));
    }

    public Set<String> getRolesFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("roles", Set.class));
    }

    public boolean isTokenExpired(String token) {
        try {
            return getAllClaimsFromToken(token).getExpiration().before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.error("Error while parsing token for expiration check: {}", e.getMessage());
            return true;
        }
    }

    public String generateJwt(InitDataUser initDataUser) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", initDataUser.getId());
        claims.put("username", initDataUser.getUsername());
        claims.put("roles", Set.of("USER"));
        return Jwts.builder()
                .claims(claims)
                .subject(initDataUser.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + lifeTime))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    private Claims getAllClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                    .verifyWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        }catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException("Error while parsing JWT: " + exception.getMessage(), exception);
        }
    }

    private <T> T getClaimFromToken(String token, Function<Claims, T> claimResolver) {
        return claimResolver.apply(getAllClaimsFromToken(token));
    }

}
