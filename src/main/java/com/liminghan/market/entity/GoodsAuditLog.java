package com.liminghan.market.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("goods_audit_log")
public class GoodsAuditLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private Long goodsId;

    private Long adminId;

    private String result;

    private String reason;

    private LocalDateTime createdAt;
}
