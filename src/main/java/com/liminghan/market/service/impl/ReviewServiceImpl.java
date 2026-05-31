package com.liminghan.market.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.liminghan.market.common.BusinessException;
import com.liminghan.market.common.ErrorCode;
import com.liminghan.market.entity.MarketOrder;
import com.liminghan.market.entity.MarketReview;
import com.liminghan.market.mapper.MarketReviewMapper;
import com.liminghan.market.security.SecurityContextUtil;
import com.liminghan.market.service.OrderService;
import com.liminghan.market.service.ReviewService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ReviewServiceImpl extends ServiceImpl<MarketReviewMapper, MarketReview> implements ReviewService {

    private final OrderService orderService;

    public ReviewServiceImpl(OrderService orderService) {
        this.orderService = orderService;
    }

    @Override
    public MarketReview createReview(Long orderId, Integer rating, String content) {
        Long userId = SecurityContextUtil.currentUser().getUserId();
        MarketOrder order = orderService.getById(orderId);
        if (order == null) {
            throw new BusinessException(ErrorCode.NOT_FOUND, "order not found");
        }
        if (!"FINISHED".equals(order.getStatus())) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "only FINISHED orders can be reviewed");
        }
        if (!order.getBuyerId().equals(userId)) {
            throw new BusinessException(ErrorCode.FORBIDDEN, "only the buyer can review");
        }
        boolean exists = lambdaQuery()
                .eq(MarketReview::getOrderId, orderId)
                .exists();
        if (exists) {
            throw new BusinessException(ErrorCode.PARAM_ERROR, "order already reviewed");
        }
        MarketReview review = new MarketReview();
        review.setOrderId(orderId);
        review.setReviewerId(userId);
        review.setTargetUserId(order.getSellerId());
        review.setGoodsId(order.getGoodsId());
        review.setRating(rating);
        review.setContent(content);
        review.setCreatedAt(LocalDateTime.now());
        save(review);
        return review;
    }

    @Override
    public List<MarketReview> listGoodsReviews(Long goodsId) {
        return lambdaQuery()
                .eq(MarketReview::getGoodsId, goodsId)
                .orderByDesc(MarketReview::getCreatedAt)
                .list();
    }

    @Override
    public List<MarketReview> listUserReviews(Long userId) {
        return lambdaQuery()
                .eq(MarketReview::getTargetUserId, userId)
                .orderByDesc(MarketReview::getCreatedAt)
                .list();
    }
}
