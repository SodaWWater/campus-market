package com.liminghan.market.controller;

import com.liminghan.market.common.Result;
import com.liminghan.market.entity.MarketFavorite;
import com.liminghan.market.service.FavoriteService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/favorites")
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(FavoriteService favoriteService) {
        this.favoriteService = favoriteService;
    }

    @Operation(summary = "List my favorites")
    @GetMapping("/my")
    public Result<List<MarketFavorite>> myFavorites() {
        return Result.success(favoriteService.listMyFavorites());
    }
}
