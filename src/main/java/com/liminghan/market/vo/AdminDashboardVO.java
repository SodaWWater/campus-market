package com.liminghan.market.vo;

import lombok.Data;

@Data
public class AdminDashboardVO {

    private Long userCount;

    private Long goodsCount;

    private Long pendingGoodsCount;

    private Long onSaleGoodsCount;

    private Long orderCount;

    private Long createdOrderCount;

    private Long paidOrderCount;
}
