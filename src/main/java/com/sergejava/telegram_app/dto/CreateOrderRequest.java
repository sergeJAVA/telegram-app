package com.sergejava.telegram_app.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CreateOrderRequest {

    @JsonProperty("phone_number")
    @NotBlank(message = "The phone number must not be blank.")
    @Size(min = 11, max = 12, message = "The phone number must be between 11 and 12 characters long.")
    private String phoneNumber;

    @JsonProperty("delivery_address")
    @NotBlank(message = "The delivery address must not be blank.")
    @Size(max = 255, message = "The maximum length of the delivery address is 255 characters.")
    private String deliveryAddress;

}
