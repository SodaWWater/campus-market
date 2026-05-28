package com.liminghan.market.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class GoodsUpdateRequest {

    private Long categoryId;

    private String title;

    private String description;

    private BigDecimal price;

    private String status;
}
