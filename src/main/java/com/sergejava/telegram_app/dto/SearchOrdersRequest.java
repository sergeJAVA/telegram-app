package com.sergejava.telegram_app.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchOrdersRequest {

    @NotNull
    @NotBlank
    private String status;

    @Min(value = 0, message = "page must be >= 0")
    @NotNull
    private Integer page;

    @Min(value = 5, message = "size min value is 5")
    @Max(value = 50, message = "size max value is 50")
    @NotNull
    private Integer size;

}
