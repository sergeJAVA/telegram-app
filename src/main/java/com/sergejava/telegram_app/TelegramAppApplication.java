package com.sergejava.telegram_app;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class TelegramAppApplication {

	public static void main(String[] args) {
		SpringApplication.run(TelegramAppApplication.class, args);
	}

}
