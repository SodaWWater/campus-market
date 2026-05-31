package com.liminghan.market.service;

import com.liminghan.market.entity.MarketFavorite;

import java.util.List;

public interface FavoriteService {

    void favorite(Long goodsId);

    void unfavorite(Long goodsId);

    List<MarketFavorite> listMyFavorites();

    boolean isFavorited(Long userId, Long goodsId);
}
