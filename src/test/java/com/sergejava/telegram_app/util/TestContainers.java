package com.sergejava.telegram_app.util;

import org.springframework.context.annotation.Profile;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;

@Profile("test")
public abstract class TestContainers {

    private static final String TEST_BOT_TOKEN = "gtFbrJQQIFWaBDAkAqJA8sD0LWlvAMbqS4sm" +
            "p961Ov1y1ZvuPUfEIP2TxtA5lOMIyd09gUOOT4OyDZup7dU499rbiVk" +
            "Ufu6akCgl7hkUp7FMDEaMHq5vUbQwI4c98bmLYCaF5bhzyoXRfcKF0DbTIq" +
            "IgimyhLOc8qJzWDZUXHsoo31QguQ3Pjuv9vxZLkjR25olhjJR0tma7wQzyC2hU" +
            "M271uLZjATyLJaDSlfu8j392tO1ofgz5xPqL2vbdstmh";

    static final PostgreSQLContainer postgres =
            new PostgreSQLContainer("postgres:17.4")
                    .withUsername("test")
                    .withPassword("test");

    static final GenericContainer<?> redis = new GenericContainer<>("redis:8.2.1")
            .withExposedPorts(6379)
            .withCommand("redis-server --requirepass testpass");

    {
        postgres.start();
        redis.start();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.datasource.username", postgres::getUsername);

        registry.add("spring.data.redis.host", redis::getHost);
        registry.add("spring.data.redis.password", () -> "testpass");
        registry.add("spring.data.redis.port", () -> redis.getMappedPort(6379));
        registry.add("telegram.bot.token", () -> TEST_BOT_TOKEN);
    }

}
