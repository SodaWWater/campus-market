package com.liminghan.market.service;

import com.liminghan.market.entity.MarketReview;

import java.util.List;

public interface ReviewService {

    MarketReview createReview(Long orderId, Integer rating, String content);

    List<MarketReview> listGoodsReviews(Long goodsId);

    List<MarketReview> listUserReviews(Long userId);
}
