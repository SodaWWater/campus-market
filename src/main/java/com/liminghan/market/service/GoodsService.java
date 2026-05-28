package com.liminghan.market.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.liminghan.market.dto.GoodsCreateRequest;
import com.liminghan.market.dto.GoodsUpdateRequest;
import com.liminghan.market.entity.MarketGoods;

public interface GoodsService extends IService<MarketGoods> {

    MarketGoods createGoods(GoodsCreateRequest request);

    MarketGoods updateGoods(Long id, GoodsUpdateRequest request);

    void deleteGoods(Long id);

    IPage<MarketGoods> pageGoods(long current, long size, String keyword, Long categoryId);

    MarketGoods getGoods(Long id);
}
