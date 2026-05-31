# 简历项目描述

## 校园二手交易平台

**技术栈**：Java 17 · Spring Boot 3.3 · Spring Security 6 · JWT · MyBatis-Plus · MySQL · Redis · Vue 3 · Element Plus · Vite · Docker

### 项目描述

个人学习、复现与二次开发项目。面向校园二手交易场景，实现用户端商品浏览、下单支付、收藏消息评价和后台管理端商品审核、数据看板、操作日志等功能，提供 Vue 3 前后端分离演示。

### 主要工作

- 设计 13 张数据表，包含用户、商品（多图+审核）、订单、支付记录、操作日志、收藏、消息、评价、举报
- 使用 Spring Security 6 配置 SecurityFilterChain + JWT 过滤器，区分 USER/ADMIN 角色
- 注册使用 BCrypt 加密密码；发布商品 sellerId 从 SecurityContext 获取，防止伪造
- 商品审核流：发布 → PENDING_AUDIT → 管理员通过/驳回 → ON_SALE
- 订单创建使用条件 UPDATE（WHERE status='ON_SALE'）防并发，与订单插入在同一事务
- 分类列表和热商品页使用 Redis 缓存 + 降级查询 MySQL
- 订单状态变更写 order_log + payment_record 留痕（事务内同步完成）
- 实现收藏（计数同步）、站内消息（会话+未读数）、交易评价（仅已完成订单）、举报提交
- 后台管理：数据看板、商品审核、分类管理、用户启用/禁用、举报处理、管理员操作日志
- Vue 3 + Vue Router 实现用户端和后台端独立布局，19 条路由 + 导航守卫，Axios 拦截器自动注入 JWT
- Docker Compose 一键启动 MySQL + Redis，GitHub Actions CI 自动编译

### 简历写法建议

```
项目名称：校园二手交易平台
时间：2026.03 — 2026.05
角色：独立开发

描述：
基于 Spring Boot + Vue 3 开发的校园二手交易平台，涵盖用户端商品浏览交易
和后台管理审核功能。实现了完整的商品发布、审核、下单、支付状态流转，以及
收藏、站内消息、评价等用户互动功能。

技术点：
后端：Spring Boot 3、Spring Security 6、JWT 鉴权、MyBatis-Plus ORM、
      Redis 缓存（带降级）、MySQL 数据库设计（13 张表）
前端：Vue 3 + Element Plus + Vue Router，用户端/后台端独立布局
工程化：Docker Compose 环境、GitHub Actions CI

成果：完整可演示的交易闭环，支持游客浏览、用户交易、管理员审核
```

### 不要夸大

- 不写精通（technically correct: 掌握/熟练使用）
- 不写高并发、分布式、微服务架构
- 不写真实支付（本项目是状态模拟）
- 不写复杂 RBAC（只有 USER / ADMIN 两种角色）
- 不写千万级数据（本项目是学习演示规模）
