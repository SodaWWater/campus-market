package com.liminghan.market.controller;

import com.liminghan.market.common.Result;
import com.liminghan.market.dto.CategoryRequest;
import com.liminghan.market.dto.HandleReportRequest;
import com.liminghan.market.dto.RejectGoodsRequest;
import com.liminghan.market.entity.AdminOperationLog;
import com.liminghan.market.entity.MarketCategory;
import com.liminghan.market.entity.MarketGoods;
import com.liminghan.market.entity.MarketOrder;
import com.liminghan.market.entity.MarketReport;
import com.liminghan.market.entity.SysUser;
import com.liminghan.market.service.AdminService;
import com.liminghan.market.service.CategoryService;
import com.liminghan.market.service.ReportService;
import com.liminghan.market.vo.AdminDashboardVO;
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
    private final ReportService reportService;

    public AdminController(AdminService adminService, CategoryService categoryService, ReportService reportService) {
        this.adminService = adminService;
        this.categoryService = categoryService;
        this.reportService = reportService;
    }

    @Operation(summary = "Admin dashboard")
    @GetMapping("/dashboard")
    public Result<AdminDashboardVO> dashboard() {
        return Result.success(adminService.dashboard());
    }

    @Operation(summary = "List users")
    @GetMapping("/users")
    public Result<List<SysUser>> listUsers() {
        return Result.success(adminService.listUsers());
    }

    @Operation(summary = "Disable user")
    @PutMapping("/users/{id}/disable")
    public Result<SysUser> disableUser(@PathVariable Long id) {
        return Result.success(adminService.disableUser(id));
    }

    @Operation(summary = "Enable user")
    @PutMapping("/users/{id}/enable")
    public Result<SysUser> enableUser(@PathVariable Long id) {
        return Result.success(adminService.enableUser(id));
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

    @Operation(summary = "List pending audit goods")
    @GetMapping("/goods/pending")
    public Result<List<MarketGoods>> pendingGoods() {
        return Result.success(adminService.listGoods().stream()
                .filter(goods -> "PENDING_AUDIT".equals(goods.getStatus()))
                .toList());
    }

    @Operation(summary = "Approve goods")
    @PutMapping("/goods/{id}/approve")
    public Result<MarketGoods> approveGoods(@PathVariable Long id) {
        return Result.success(adminService.approveGoods(id));
    }

    @Operation(summary = "Reject goods")
    @PutMapping("/goods/{id}/reject")
    public Result<MarketGoods> rejectGoods(@PathVariable Long id, @Valid @RequestBody RejectGoodsRequest request) {
        return Result.success(adminService.rejectGoods(id, request.getReason()));
    }

    @Operation(summary = "List all orders")
    @GetMapping("/orders")
    public Result<List<MarketOrder>> listOrders() {
        return Result.success(adminService.listOrders());
    }

    @Operation(summary = "Create category")
    @PostMapping("/categories")
    public Result<MarketCategory> createCategory(@Valid @RequestBody CategoryRequest request) {
        return Result.success(categoryService.createCategory(request));
    }

    @Operation(summary = "Update category")
    @PutMapping("/categories/{id}")
    public Result<MarketCategory> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequest request) {
        return Result.success(categoryService.updateCategory(id, request));
    }

    @Operation(summary = "Enable category")
    @PutMapping("/categories/{id}/enable")
    public Result<MarketCategory> enableCategory(@PathVariable Long id) {
        return Result.success(adminService.enableCategory(id));
    }

    @Operation(summary = "Disable category")
    @PutMapping("/categories/{id}/disable")
    public Result<MarketCategory> disableCategory(@PathVariable Long id) {
        return Result.success(adminService.disableCategory(id));
    }

    @Operation(summary = "Delete category")
    @DeleteMapping("/categories/{id}")
    public Result<String> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return Result.success("ok");
    }

    @Operation(summary = "List operation logs")
    @GetMapping("/operation-logs")
    public Result<List<AdminOperationLog>> operationLogs() {
        return Result.success(adminService.listOperationLogs());
    }

    @Operation(summary = "List all reports")
    @GetMapping("/reports")
    public Result<List<MarketReport>> listReports() {
        return Result.success(reportService.listAllReports());
    }

    @Operation(summary = "Handle report")
    @PutMapping("/reports/{id}/handle")
    public Result<MarketReport> handleReport(@PathVariable Long id, @Valid @RequestBody HandleReportRequest request) {
        return Result.success(reportService.handleReport(id, request.getStatus(), request.getHandleResult()));
    }
}
