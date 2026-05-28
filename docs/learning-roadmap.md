# 学习路线

## 第 1 步：跑通项目

导入 `scripts/init.sql` 和 `scripts/sample-data.sql`，启动项目，访问 `GET /api/health`。

## 第 2 步：看认证模块

阅读 `AuthController`、`AuthServiceImpl`、`JwtUtil`、`JwtAuthenticationFilter`、`SecurityConfig`。

## 第 3 步：看商品模块

阅读 `GoodsController` 和 `GoodsServiceImpl`，理解 sellerId 为什么来自当前登录用户。

## 第 4 步：看分类缓存

阅读 `CategoryController`、`AdminController` 和 `CategoryServiceImpl`，理解 Redis 缓存和删除缓存。

## 第 5 步：看订单事务

阅读 `OrderController` 和 `OrderServiceImpl`，理解创建、支付、取消、完成的状态流转。

## 第 6 步：看后台管理

阅读 `AdminController`、`AdminServiceImpl` 和 `SecurityConfig`，理解 ADMIN 接口如何限制。

## 第 7 步：背面试题

按 `docs/interview-qa.md` 的 30 个问题准备，每个问题都要能指出代码位置。
