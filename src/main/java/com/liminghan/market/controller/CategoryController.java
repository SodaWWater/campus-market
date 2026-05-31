package com.liminghan.market.controller;

import com.liminghan.market.common.Result;
import com.liminghan.market.entity.MarketCategory;
import com.liminghan.market.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @Operation(summary = "List categories")
    @GetMapping({"/api/categories", "/api/category/list"})
    public Result<List<MarketCategory>> list() {
        return Result.success(categoryService.listCategories());
    }
}
