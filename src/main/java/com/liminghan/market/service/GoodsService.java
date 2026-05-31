package com.liminghan.market.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liminghan.market.dto.GoodsCreateRequest;
import com.liminghan.market.dto.GoodsUpdateRequest;
import com.liminghan.market.entity.GoodsAuditLog;
import com.liminghan.market.entity.MarketGoods;

import java.util.List;

public interface GoodsService extends IService<MarketGoods> {

    MarketGoods createGoods(GoodsCreateRequest request);

    MarketGoods updateGoods(Long id, GoodsUpdateRequest request);

    MarketGoods offShelfMyGoods(Long id);

    IPage<MarketGoods> pageGoods(long current,
                                 long size,
                                 String keyword,
                                 Long categoryId,
                                 String conditionLevel,
                                 java.math.BigDecimal minPrice,
                                 java.math.BigDecimal maxPrice);

    IPage<MarketGoods> pageMyGoods(long current, long size, String status);

    MarketGoods getGoods(Long id);

    MarketGoods getPublicGoods(Long id);

    MarketGoods submitAudit(Long id);

    MarketGoods approveGoods(Long id);

    MarketGoods rejectGoods(Long id, String reason);

    List<GoodsAuditLog> listAuditLogs(Long goodsId);
}
