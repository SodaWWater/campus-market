# 简历描述

## 校园二手交易与后台管理系统

技术栈：Spring Boot 3、Java 17、Spring Security 6、JWT、MyBatis-Plus、MySQL、Redis、RabbitMQ、Vue 3、Element Plus。

项目描述：个人学习、复现与二次开发项目，面向校园二手交易场景，实现注册登录、JWT 鉴权、商品发布与查询、分类缓存、订单状态流转、后台管理，并使用 RabbitMQ 记录订单事件日志，提供 Vue 3 前端演示。

主要工作：

- 设计 `sys_user`、`market_category`、`market_goods`、`market_order`、`market_order_event_log` 表。
- 使用 Spring Security 6 配置 `SecurityFilterChain` 和 JWT 过滤器。
- 注册使用 BCrypt 加密密码，登录返回 token、userId、username、role。
- 发布商品时 sellerId 从当前登录用户获取。
- 分类列表和热商品页使用 Redis 缓存，缓存失败时降级查 MySQL。
- 创建订单使用事务，保证订单和商品状态同步修改。
- 使用 RabbitMQ 发送订单事件并落库为事件日志。
- 使用 Vue 3 + Pinia 做登录、商品、分类、订单和后台入口。

不要夸大：不要写精通、高并发、分布式、微服务、真实支付。
