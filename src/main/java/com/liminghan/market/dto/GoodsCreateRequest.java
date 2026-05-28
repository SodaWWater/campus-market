package com.liminghan.market.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class GoodsCreateRequest {

    @NotNull(message = "categoryId cannot be null")
    private Long categoryId;

    @NotBlank(message = "title cannot be blank")
    private String title;

    private String description;

    @NotNull(message = "price cannot be null")
    private BigDecimal price;
}
