package com.liminghan.market.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liminghan.market.common.BusinessException;
import com.liminghan.market.common.ErrorCode;
import com.liminghan.market.dto.GoodsCreateRequest;
import com.liminghan.market.dto.GoodsUpdateRequest;
import com.liminghan.market.entity.MarketGoods;
import com.liminghan.market.mapper.MarketGoodsMapper;
import com.liminghan.market.security.SecurityContextUtil;
import com.liminghan.market.service.GoodsService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Set;

@Service
public class GoodsServiceImpl extends ServiceImpl<MarketGoodsMapper, MarketGoods> implements GoodsService {

    private static final Set<String> GOODS_STATUS = Set.of("ON_SALE", "LOCKED", "SOLD", "OFF_SHELF");

    @Override
    public MarketGoods createGoods(GoodsCreateRequest request) {
        MarketGoods goods = new MarketGoods();
        goods.setSellerId(SecurityContextUtil.currentUser().getUserId());
        goods.setCategoryId(request.getCategoryId());
        goods.setTitle(request.getTitle());
        goods.setDescription(request.getDescription());
        goods.setPrice(request.getPrice());
        goods.setStatus("ON_SALE");
        goods.setViewCount(0);
        goods.setCreatedAt(LocalDateTime.now());
        goods.setUpdatedAt(LocalDateTime.now());
        save(goods);
        return goods;
    }

    @Override
    public MarketGoods updateGoods(Long id, GoodsUpdateRequest request) {
        MarketGoods goods = getGoods(id);
        Long currentUserId = SecurityContextUtil.currentUser().getUserId();
        if (!goods.getSellerId().equals(currentUserId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "only seller can update goods");
        }
        if (request.getCategoryId() != null) {
            goods.setCategoryId(request.getCategoryId());
        }
        if (request.getTitle() != null) {
            goods.setTitle(request.getTitle());
        }
        if (request.getDescription() != null) {
            goods.setDescription(request.getDescription());
        }
        if (request.getPrice() != null) {
            goods.setPrice(request.getPrice());
        }
        if (request.getStatus() != null) {
            String status = request.getStatus().toUpperCase();
            if (!GOODS_STATUS.contains(status)) {
                throw new BusinessException(ErrorCode.PARAM_ERROR, "invalid goods status");
            }
            goods.setStatus(status);
        }
        goods.setUpdatedAt(LocalDateTime.now());
        updateById(goods);
        return goods;
    }

    @Override
    public void deleteGoods(Long id) {
        MarketGoods goods = getGoods(id);
        Long currentUserId = SecurityContextUtil.currentUser().getUserId();
        if (!goods.getSellerId().equals(currentUserId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "only seller can delete goods");
        }
        removeById(id);
    }

    @Override
    public IPage<MarketGoods> pageGoods(long current, long size, String keyword, Long categoryId) {
        return lambdaQuery()
                .like(keyword != null && !keyword.isBlank(), MarketGoods::getTitle, keyword)
                .eq(categoryId != null, MarketGoods::getCategoryId, categoryId)
                .orderByDesc(MarketGoods::getCreatedAt)
                .page(Page.of(current, size));
    }

    @Override
    public MarketGoods getGoods(Long id) {
        MarketGoods goods = getById(id);
        if (goods == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "goods not found");
        }
        return goods;
    }
}
