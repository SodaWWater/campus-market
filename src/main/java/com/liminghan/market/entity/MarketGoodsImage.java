package com.liminghan.market.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("market_goods_image")
public class MarketGoodsImage {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long goodsId;

    private String imageUrl;

    private Integer sort;

    private LocalDateTime createdAt;
}
