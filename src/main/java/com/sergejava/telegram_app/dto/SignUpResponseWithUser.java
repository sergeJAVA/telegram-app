package com.sergejava.telegram_app.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

@Getter
@Setter
@NoArgsConstructor
public class SignUpResponseWithUser {

    private String message;
    private String status;
    private UserDTO user;

    public SignUpResponseWithUser(String message, HttpStatus status, UserDTO user) {
        this.message = message;
        this.status = status.getReasonPhrase();
        this.user = user;
    }

}
