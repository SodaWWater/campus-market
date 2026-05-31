package com.liminghan.market.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liminghan.market.common.Result;
import com.liminghan.market.dto.GoodsCreateRequest;
import com.liminghan.market.dto.GoodsUpdateRequest;
import com.liminghan.market.entity.MarketGoods;
import com.liminghan.market.service.FavoriteService;
import com.liminghan.market.service.GoodsService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/goods")
public class GoodsController {

    private final GoodsService goodsService;
    private final FavoriteService favoriteService;

    public GoodsController(GoodsService goodsService, FavoriteService favoriteService) {
        this.goodsService = goodsService;
        this.favoriteService = favoriteService;
    }

    @Operation(summary = "Create goods")
    @PostMapping
    public Result<MarketGoods> create(@Valid @RequestBody GoodsCreateRequest request) {
        return Result.success(goodsService.createGoods(request));
    }

    @Operation(summary = "Update goods")
    @PutMapping("/{id}")
    public Result<MarketGoods> update(@PathVariable Long id, @RequestBody GoodsUpdateRequest request) {
        return Result.success(goodsService.updateGoods(id, request));
    }

    @Operation(summary = "Page goods")
    @GetMapping("/page")
    public Result<IPage<MarketGoods>> page(@RequestParam(defaultValue = "1") long current,
                                           @RequestParam(defaultValue = "10") long size,
                                           @RequestParam(required = false) String keyword,
                                           @RequestParam(required = false) Long categoryId,
                                           @RequestParam(required = false) String conditionLevel,
                                           @RequestParam(required = false) java.math.BigDecimal minPrice,
                                           @RequestParam(required = false) java.math.BigDecimal maxPrice) {
        return Result.success(goodsService.pageGoods(current, size, keyword, categoryId, conditionLevel, minPrice, maxPrice));
    }

    @Operation(summary = "Get goods detail")
    @GetMapping("/{id}")
    public Result<MarketGoods> get(@PathVariable Long id) {
        return Result.success(goodsService.getPublicGoods(id));
    }

    @Operation(summary = "List my goods")
    @GetMapping("/my")
    public Result<IPage<MarketGoods>> myGoods(@RequestParam(defaultValue = "1") long current,
                                              @RequestParam(defaultValue = "10") long size,
                                              @RequestParam(required = false) String status) {
        return Result.success(goodsService.pageMyGoods(current, size, status));
    }

    @Operation(summary = "Submit goods audit")
    @PutMapping("/{id}/submit")
    public Result<MarketGoods> submit(@PathVariable Long id) {
        return Result.success(goodsService.submitAudit(id));
    }

    @Operation(summary = "Off shelf my goods")
    @PutMapping("/{id}/off")
    public Result<MarketGoods> off(@PathVariable Long id) {
        return Result.success(goodsService.offShelfMyGoods(id));
    }

    @Operation(summary = "Favorite goods")
    @PostMapping("/{id}/favorite")
    public Result<String> favorite(@PathVariable Long id) {
        favoriteService.favorite(id);
        return Result.success("ok");
    }

    @Operation(summary = "Unfavorite goods")
    @DeleteMapping("/{id}/favorite")
    public Result<String> unfavorite(@PathVariable Long id) {
        favoriteService.unfavorite(id);
        return Result.success("ok");
    }
}
