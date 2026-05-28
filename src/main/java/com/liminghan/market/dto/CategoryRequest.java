package com.liminghan.market.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CategoryRequest {

    @NotBlank(message = "category name cannot be blank")
    private String name;

    private Integer sort;

    private String status;
}
