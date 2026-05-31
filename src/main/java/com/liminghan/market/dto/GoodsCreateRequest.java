package com.liminghan.market.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class GoodsCreateRequest {

    @NotNull(message = "categoryId cannot be null")
    private Long categoryId;

    @NotBlank(message = "title cannot be blank")
    private String title;

    private String description;

    @NotNull(message = "price cannot be null")
    @DecimalMin(value = "0.01", message = "price must be greater than 0")
    private BigDecimal price;

    private String conditionLevel;

    private String tradeLocation;

    private List<String> imageUrls;
}
