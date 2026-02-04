package com.sergejava.telegram_app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrderChangeStatusRequest {

    @Positive
    @NotNull
    private Long id;

    @NotBlank(message = "The status must not be blank.")
    private String status;

}
