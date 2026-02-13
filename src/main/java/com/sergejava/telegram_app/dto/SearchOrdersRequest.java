package com.sergejava.telegram_app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @Size
    private Integer page;

    @Size
    private Integer size;

}
