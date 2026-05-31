# 学习路线

## 推荐学习顺序

1. 跑通 Docker + 后端 + 前端，登录 admin 账号体验完整流程
2. 看 `SecurityConfig` 和 `JwtAuthenticationFilter`，理解鉴权链路
3. 看 `sys_user` → `market_goods` → `market_order` 核心表关系
4. 看 `GoodsServiceImpl`，理解商品发布 → 审核 → 上架流程
5. 看 `OrderServiceImpl`，理解条件 UPDATE 防并发 + 状态流转
6. 看 `PaymentServiceImpl`，理解支付记录 + 日志的事务写入
7. 看 `CategoryServiceImpl`，理解 Redis 缓存查询、写入、删除
8. 看 `FavoriteServiceImpl`，理解收藏计数同步
9. 看 `MessageServiceImpl`，理解会话列表 + 未读数查询
10. 看 `ReviewServiceImpl`，理解评价业务约束
11. 看 `AdminServiceImpl`，理解后台操作 + 操作日志记录
12. 启动前端，体验用户端和后台端的路由和布局差异

## 7 天学习计划

| 天 | 内容 | 关键文件 |
|----|------|----------|
| Day 1 | 跑通 Docker + 后端 + 前端，登录测试 | `docker-compose.yml`、`README.md` |
| Day 2 | 理解认证鉴权：Spring Security 6 + JWT | `SecurityConfig.java`、`JwtAuthenticationFilter.java` |
| Day 3 | 理解商品与审核：发布、审核、上架 | `GoodsController.java`、`GoodsServiceImpl.java` |
| Day 4 | 理解订单与支付：创建、锁定、支付、取消 | `OrderServiceImpl.java`、`PaymentServiceImpl.java` |
| Day 5 | 理解缓存与降级：Redis Cache-Aside | `CategoryServiceImpl.java` |
| Day 6 | 理解用户互动：收藏、消息、评价、举报 | `FavoriteServiceImpl.java`、`MessageServiceImpl.java` |
| Day 7 | 前端架构 + 用自己的话复述项目 | `frontend/src/router/`、`frontend/src/views/` |
