# 学习路线

1. 跑通注册和登录，理解 BCrypt 与 JWT 返回值。
2. 看 `SecurityConfig` 和 `JwtAuthenticationFilter`，理解哪些接口放行、哪些接口需要角色。
3. 看 `sys_user`、`market_goods`、`market_order` 等表和 Entity 映射。
4. 看 `GoodsServiceImpl`，理解 sellerId 为什么来自当前登录用户。
5. 看 `CategoryServiceImpl`，理解 Redis 缓存查询、写入和删除。
6. 看 `OrderServiceImpl`，理解创建订单事务和状态流转。
7. 看 `OrderEventConsumer`，理解 RabbitMQ 如何做订单事件日志。
8. 启动 `frontend`，按 `docs/api.md` 的顺序演示接口。

## 7 天计划

- Day 1：跑通后端、导入 SQL。
- Day 2：理解登录注册和 JWT。
- Day 3：理解 Spring Security 6 配置。
- Day 4：理解商品和分类接口。
- Day 5：理解 Redis 缓存降级。
- Day 6：理解订单事务和 RabbitMQ 事件。
- Day 7：用自己的话复述项目，并准备接口演示。
