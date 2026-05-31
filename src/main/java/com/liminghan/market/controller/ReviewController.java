package com.liminghan.market.controller;

import com.liminghan.market.common.Result;
import com.liminghan.market.dto.CreateReviewRequest;
import com.liminghan.market.entity.MarketReview;
import com.liminghan.market.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(ReviewService reviewService) {
        this.reviewService = reviewService;
    }

    @Operation(summary = "Create review")
    @PostMapping
    public Result<MarketReview> create(@Valid @RequestBody CreateReviewRequest request) {
        return Result.success(reviewService.createReview(
                request.getOrderId(), request.getRating(), request.getContent()));
    }

    @Operation(summary = "List goods reviews")
    @GetMapping("/goods/{goodsId}")
    public Result<List<MarketReview>> goodsReviews(@PathVariable Long goodsId) {
        return Result.success(reviewService.listGoodsReviews(goodsId));
    }

    @Operation(summary = "List user reviews")
    @GetMapping("/users/{userId}")
    public Result<List<MarketReview>> userReviews(@PathVariable Long userId) {
        return Result.success(reviewService.listUserReviews(userId));
    }
}
