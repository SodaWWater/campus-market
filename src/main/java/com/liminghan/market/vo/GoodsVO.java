package com.liminghan.market.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class GoodsVO {

    private Long id;

    private Long sellerId;

    private Long categoryId;

    private String title;

    private String description;

    private BigDecimal price;

    private String status;

    private Integer viewCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
