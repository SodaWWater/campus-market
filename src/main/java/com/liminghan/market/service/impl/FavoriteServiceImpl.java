package com.liminghan.market.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liminghan.market.common.BusinessException;
import com.liminghan.market.common.ErrorCode;
import com.liminghan.market.entity.MarketFavorite;
import com.liminghan.market.entity.MarketGoods;
import com.liminghan.market.mapper.MarketFavoriteMapper;
import com.liminghan.market.security.SecurityContextUtil;
import com.liminghan.market.service.FavoriteService;
import com.liminghan.market.service.GoodsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class FavoriteServiceImpl extends ServiceImpl<MarketFavoriteMapper, MarketFavorite> implements FavoriteService {

    private final GoodsService goodsService;

    public FavoriteServiceImpl(GoodsService goodsService) {
        this.goodsService = goodsService;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void favorite(Long goodsId) {
        Long userId = SecurityContextUtil.currentUser().getUserId();
        MarketGoods goods = goodsService.getById(goodsId);
        if (goods == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "goods not found");
        }
        if (isFavorited(userId, goodsId)) {
            return;
        }
        MarketFavorite favorite = new MarketFavorite();
        favorite.setUserId(userId);
        favorite.setGoodsId(goodsId);
        favorite.setCreatedAt(LocalDateTime.now());
        save(favorite);
        goods.setFavoriteCount(goods.getFavoriteCount() + 1);
        goodsService.updateById(goods);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void unfavorite(Long goodsId) {
        Long userId = SecurityContextUtil.currentUser().getUserId();
        MarketFavorite favorite = lambdaQuery()
                .eq(MarketFavorite::getUserId, userId)
                .eq(MarketFavorite::getGoodsId, goodsId)
                .one();
        if (favorite == null) {
            return;
        }
        removeById(favorite.getId());
        MarketGoods goods = goodsService.getById(goodsId);
        if (goods != null && goods.getFavoriteCount() > 0) {
            goods.setFavoriteCount(goods.getFavoriteCount() - 1);
            goodsService.updateById(goods);
        }
    }

    @Override
    public List<MarketFavorite> listMyFavorites() {
        Long userId = SecurityContextUtil.currentUser().getUserId();
        return lambdaQuery()
                .eq(MarketFavorite::getUserId, userId)
                .orderByDesc(MarketFavorite::getCreatedAt)
                .list();
    }

    @Override
    public boolean isFavorited(Long userId, Long goodsId) {
        return lambdaQuery()
                .eq(MarketFavorite::getUserId, userId)
                .eq(MarketFavorite::getGoodsId, goodsId)
                .exists();
    }
}
