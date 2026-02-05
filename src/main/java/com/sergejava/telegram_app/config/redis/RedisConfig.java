package com.sergejava.telegram_app.config.redis;

import com.sergejava.telegram_app.dto.CartDTO;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.JacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;


@Configuration
@EnableCaching
public class RedisConfig {

    @Bean
    public RedisCacheManager cacheConfiguration(RedisConnectionFactory connectionFactory) {
        JacksonJsonRedisSerializer<?> cartDTOSerializer = new JacksonJsonRedisSerializer<>(CartDTO.class);

        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put("cart", RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(10))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(cartDTOSerializer))
                .disableCachingNullValues());
        cacheConfigurations.put("jwt", RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(5))
                .disableCachingNullValues());

        return RedisCacheManager.builder(connectionFactory)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }

}
