package com.liminghan.market.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("market_order")
public class MarketOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long buyerId;

    private Long sellerId;

    private Long goodsId;

    private BigDecimal amount;

    private String status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
