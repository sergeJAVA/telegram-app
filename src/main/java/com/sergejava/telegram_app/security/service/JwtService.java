package com.sergejava.telegram_app.security.service;

import com.sergejava.telegram_app.dto.InitDataUser;
import com.sergejava.telegram_app.entity.Role;
import com.sergejava.telegram_app.entity.User;
import com.sergejava.telegram_app.security.TokenData;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * <b>Сервис для валидации, создания и парсинга JWT.</b>
 * @author sergeJAVA
 */
@Service
@Slf4j
public class JwtService {

    private final static String GUEST_JWT_PREFIX = "guest_jwt";
    private final static String JWT_PREFIX = "jwt";

    /**
     * Секретный ключ, которым подписывается JWT.
     * @author sergeJAVA
     */
    @Value("${jwt.secret}")
    private String secretKey;

    /**
     * Время жизни токена (указывается в миллисекундах).
     * @author sergeJAVA
     */
    @Value("${jwt.life-time}")
    private Long lifeTime;

    /**
     * <b>Метод для получения user_id из токена.</b>
     *
     * @param token
     * @return {@code Long}
     * @author sergeJAVA
     */
    public Long getUserIdFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("user_id", Long.class));
    }

    /**
     * <b>Метод для получения username из токена.</b>
     *
     * @param token
     * @return {@code String}
     * @author sergeJAVA
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("username", String.class));
    }

    /**
     * <b>Метод для получения ролей из токена.</b>
     *
     * <p>Примечание: роли хранятся в формате {@code ROLE_название роли}.</p>
     * @param token
     * @return {@code List<String>}
     * @author sergeJAVA
     */
    public List<String> getRolesFromToken(String token) {
        return getClaimFromToken(token, claims -> claims.get("roles", List.class));
    }

    /**
     * <b>Метод для проверки времени жизни токена.</b>
     *
     * @param token
     * @return возвращает {@code true}, если токен не истёк, или {@code false}, если истёк.
     * @author sergeJAVA
     */
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

    /**
     * <b>Метод для создания токена неавторизированному пользователю.</b>
     *
     * @param initDataUser метаданные, которые приходят от Telegram.
     * @return {@code String}
     * @author sergeJAVA
     */
    @Cacheable(value = GUEST_JWT_PREFIX, key = "#initDataUser.id")
    public String generateGuestJWT(InitDataUser initDataUser) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", initDataUser.getId());
        claims.put("username", initDataUser.getUsername());
        claims.put("roles", Set.of("ROLE_GUEST"));
        return Jwts.builder()
                .claims(claims)
                .subject(initDataUser.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + lifeTime))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    @Cacheable(value = JWT_PREFIX, key = "#user.userId")
    public String generateJWT(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("user_id", user.getUserId());
        claims.put("username", user.getUsername());
        claims.put("roles", user.getRoles().stream()
                .map(role -> "ROLE_" + role.getName())
                .collect(Collectors.toSet())
        );
        return Jwts.builder()
                .claims(claims)
                .subject(user.getUsername())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + lifeTime))
                .signWith(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .compact();
    }

    /**
     * <b>Метод для парсинга информации из токена и создания {@link TokenData}.</b>
     *
     * @param token
     * @return {@link TokenData}
     * @author sergeJAVA
     */
    public TokenData parseToken(String token) {
        return TokenData.builder()
                .token(token)
                .username(getUsernameFromToken(token))
                .authorities(getRolesFromToken(token).stream()
                        .map(SimpleGrantedAuthority::new)
                        .collect(Collectors.toSet()))
                .userTelegramId(getUserIdFromToken(token))
                .build();
    }

    /**
     * <b>Метод для получения {@code Claims} из токена.</b>
     *
     * @param token
     * @return {@code Claims}
     * @author sergeJAVA
     */
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
