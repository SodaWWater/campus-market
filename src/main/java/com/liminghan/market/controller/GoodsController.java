package com.liminghan.market.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.liminghan.market.common.Result;
import com.liminghan.market.dto.GoodsCreateRequest;
import com.liminghan.market.dto.GoodsUpdateRequest;
import com.liminghan.market.entity.MarketGoods;
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

    public GoodsController(GoodsService goodsService) {
        this.goodsService = goodsService;
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

    @Operation(summary = "Delete goods")
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        goodsService.deleteGoods(id);
        return Result.success("ok");
    }

    @Operation(summary = "Page goods")
    @GetMapping("/page")
    public Result<IPage<MarketGoods>> page(@RequestParam(defaultValue = "1") long current,
                                           @RequestParam(defaultValue = "10") long size,
                                           @RequestParam(required = false) String keyword,
                                           @RequestParam(required = false) Long categoryId) {
        return Result.success(goodsService.pageGoods(current, size, keyword, categoryId));
    }

    @Operation(summary = "Get goods detail")
    @GetMapping("/{id}")
    public Result<MarketGoods> get(@PathVariable Long id) {
        return Result.success(goodsService.getGoods(id));
    }
}
