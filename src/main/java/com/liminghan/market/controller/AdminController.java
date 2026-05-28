package com.liminghan.market.controller;

import com.liminghan.market.common.Result;
import com.liminghan.market.dto.CategoryRequest;
import com.liminghan.market.entity.MarketCategory;
import com.liminghan.market.entity.MarketGoods;
import com.liminghan.market.entity.MarketOrder;
import com.liminghan.market.entity.SysUser;
import com.liminghan.market.service.AdminService;
import com.liminghan.market.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminController {

    private final AdminService adminService;
    private final CategoryService categoryService;

    public AdminController(AdminService adminService, CategoryService categoryService) {
        this.adminService = adminService;
        this.categoryService = categoryService;
    }

    @Operation(summary = "List users")
    @GetMapping("/users")
    public Result<List<SysUser>> listUsers() {
        return Result.success(adminService.listUsers());
    }

    @Operation(summary = "List all goods")
    @GetMapping("/goods")
    public Result<List<MarketGoods>> listGoods() {
        return Result.success(adminService.listGoods());
    }

    @Operation(summary = "Off shelf goods")
    @PutMapping("/goods/{id}/off")
    public Result<MarketGoods> offShelfGoods(@PathVariable Long id) {
        return Result.success(adminService.offShelfGoods(id));
    }

    @Operation(summary = "List all orders")
    @GetMapping("/orders")
    public Result<List<MarketOrder>> listOrders() {
        return Result.success(adminService.listOrders());
    }

    @Operation(summary = "Create category")
    @PostMapping("/category")
    public Result<MarketCategory> createCategory(@Valid @RequestBody CategoryRequest request) {
        return Result.success(categoryService.createCategory(request));
    }

    @Operation(summary = "Update category")
    @PutMapping("/category/{id}")
    public Result<MarketCategory> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        return Result.success(categoryService.updateCategory(id, request));
    }

    @Operation(summary = "Delete category")
    @DeleteMapping("/category/{id}")
    public Result<String> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success("ok");
    }
}
