package com.liminghan.market.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("market_goods")
public class MarketGoods {

    @TableId(type = IdType.AUTO)
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
