# campus-market-admin 面试问答

每题按“考点、回答、不要、代码位置”准备。

## 项目介绍类

### 1. 这个项目是什么？
- 面试官想考什么：项目定位。
- 推荐回答：这是个人学习、复现与二次开发项目，做校园二手交易和后台管理，包含登录、JWT、商品、分类、订单、后台接口、Redis 缓存和 RabbitMQ 订单事件。
- 不要怎么回答：不要说成公司真实业务系统。
- 对应代码位置：`README.md`。

### 2. 核心业务链路是什么？
- 面试官想考什么：业务理解。
- 推荐回答：用户登录后发布商品，其他用户创建订单，订单创建锁定商品，支付后商品售出，取消未支付订单恢复上架。
- 不要怎么回答：不要只说增删改查。
- 对应代码位置：`OrderServiceImpl.java`。

### 3. 前端做了什么？
- 面试官想考什么：演示能力。
- 推荐回答：Vue 3 前端支持登录注册、JWT 保存、商品发布查询、分类缓存查看、订单操作和 ADMIN 后台入口。
- 不要怎么回答：不要说做了复杂管理系统。
- 对应代码位置：`frontend/src/App.vue`。

### 4. 表结构有哪些？
- 面试官想考什么：数据库设计。
- 推荐回答：用户、分类、商品、订单、订单事件日志五张表，字段下划线，Java 驼峰映射。
- 不要怎么回答：不要漏掉事件日志表。
- 对应代码位置：`scripts/init.sql`，`entity` 包。

### 5. 后台管理实现了什么？
- 面试官想考什么：权限和管理边界。
- 推荐回答：ADMIN 可查用户、商品、订单，并可下架商品；没有做复杂 RBAC。
- 不要怎么回答：不要说有菜单权限或权限表。
- 对应代码位置：`AdminController.java`，`AdminServiceImpl.java`。

## Spring Boot 类

### 6. Controller 和 Service 怎么分层？
- 面试官想考什么：代码结构。
- 推荐回答：Controller 接收请求和返回 Result，Service 处理业务校验、事务、缓存和消息发送，Mapper 负责数据库。
- 不要怎么回答：不要把业务写在 Controller。
- 对应代码位置：`controller`，`service/impl`。

### 7. 统一返回怎么做？
- 面试官想考什么：接口规范。
- 推荐回答：使用 `Result<T>` 包装 code、message、data，异常由 `GlobalExceptionHandler` 统一处理。
- 不要怎么回答：不要每个接口返回不同结构。
- 对应代码位置：`common/Result.java`，`GlobalExceptionHandler.java`。

### 8. 参数校验怎么做？
- 面试官想考什么：基础质量。
- 推荐回答：DTO 上使用 validation 注解，Controller 使用 `@Valid` 触发校验。
- 不要怎么回答：不要完全依赖前端校验。
- 对应代码位置：`dto` 包。

### 9. 为什么使用 MyBatis-Plus？
- 面试官想考什么：技术选型。
- 推荐回答：减少基础 CRUD 样板代码，同时保留 Wrapper 条件查询，适合这个学习项目。
- 不要怎么回答：不要说 MyBatis-Plus 能自动解决所有复杂 SQL。
- 对应代码位置：`mapper` 包，`service/impl`。

### 10. Swagger 有什么用？
- 面试官想考什么：接口调试。
- 推荐回答：通过 SpringDoc 提供 Swagger UI，方便本地查看和测试接口。
- 不要怎么回答：不要把 Swagger 当鉴权方案。
- 对应代码位置：`OpenApiConfig.java`。

## MyBatis / MySQL 类

### 11. 商品分页怎么实现？
- 面试官想考什么：分页查询。
- 推荐回答：使用 MyBatis-Plus `Page` 和 QueryWrapper，支持关键词模糊查询、分类筛选。
- 不要怎么回答：不要把所有数据查出后内存分页。
- 对应代码位置：`GoodsServiceImpl#pageGoods`。

### 12. sellerId 从哪里来？
- 面试官想考什么：安全意识。
- 推荐回答：发布商品时 sellerId 从 SecurityContext 当前登录用户获取，不允许前端传 sellerId。
- 不要怎么回答：不要相信前端传入的卖家 id。
- 对应代码位置：`GoodsServiceImpl#createGoods`，`SecurityContextUtil.java`。

### 13. 订单创建为什么要事务？
- 面试官想考什么：一致性。
- 推荐回答：创建订单和修改商品状态必须一起成功或失败，避免订单创建了但商品没锁定。
- 不要怎么回答：不要忽略半成功问题。
- 对应代码位置：`OrderServiceImpl#createOrder`。

### 14. 商品状态有哪些？
- 面试官想考什么：状态建模。
- 推荐回答：`ON_SALE`、`LOCKED`、`SOLD`、`OFF_SHELF`。
- 不要怎么回答：不要只用布尔字段表示复杂状态。
- 对应代码位置：`entity/MarketGoods.java`，`GoodsStatus`。

### 15. 订单状态有哪些？
- 面试官想考什么：业务流转。
- 推荐回答：`CREATED`、`PAID`、`CANCELED`、`FINISHED`。
- 不要怎么回答：不要混淆商品状态和订单状态。
- 对应代码位置：`entity/MarketOrder.java`，`OrderStatus`。

## Redis 类

### 16. 分类缓存怎么做？
- 面试官想考什么：缓存基本流程。
- 推荐回答：先查 Redis 的 `market:category:list`，没有再查 MySQL，查完写回；新增、修改、删除后删除缓存。
- 不要怎么回答：不要更新数据库后忘记删缓存。
- 对应代码位置：`CategoryServiceImpl.java`。

### 17. Redis 不可用怎么办？
- 面试官想考什么：降级能力。
- 推荐回答：捕获 Redis 异常后直接查 MySQL 或继续主流程，Redis 只作为优化。
- 不要怎么回答：不要让缓存故障导致业务不可用。
- 对应代码位置：`CategoryServiceImpl.java`，`GoodsServiceImpl.java`。

### 18. 热商品缓存是什么？
- 面试官想考什么：缓存场景选择。
- 推荐回答：缓存第一页未筛选商品，key 是 `market:goods:hot`，商品变化后删除。
- 不要怎么回答：不要缓存所有分页组合。
- 对应代码位置：`GoodsServiceImpl#pageGoods`。

### 19. 为什么用删除缓存？
- 面试官想考什么：一致性策略。
- 推荐回答：对本项目来说删除缓存简单可靠，下次查询再回源 MySQL，降低更新缓存写错的风险。
- 不要怎么回答：不要说缓存永远一致。
- 对应代码位置：`CategoryServiceImpl`，`AdminServiceImpl`。

### 20. Redis TTL 怎么配置？
- 面试官想考什么：配置化。
- 推荐回答：分类和热商品 TTL 放在 `application.yml` 的 `app.cache` 下。
- 不要怎么回答：不要把 TTL 写死到多个地方。
- 对应代码位置：`application.yml`。

## JWT / Spring Security 类

### 21. Spring Security 6 怎么配置？
- 面试官想考什么：版本用法。
- 推荐回答：使用 `SecurityFilterChain` Bean 配置放行、鉴权和角色，不使用 `WebSecurityConfigurerAdapter`。
- 不要怎么回答：不要写旧版配置类。
- 对应代码位置：`SecurityConfig.java`。

### 22. JWT Filter 做什么？
- 面试官想考什么：认证链路。
- 推荐回答：继承 `OncePerRequestFilter`，解析 Bearer token，有效时加载用户并写入 SecurityContext。
- 不要怎么回答：不要在每个 Controller 里手动解析 token。
- 对应代码位置：`JwtAuthenticationFilter.java`。

### 23. 密码怎么保存？
- 面试官想考什么：密码安全。
- 推荐回答：注册时使用 BCrypt 加密，登录时用 `PasswordEncoder.matches` 校验，样例 SQL 也存 BCrypt hash。
- 不要怎么回答：不要明文保存密码。
- 对应代码位置：`AuthServiceImpl.java`，`scripts/sample-data.sql`。

### 24. ADMIN 接口如何限制？
- 面试官想考什么：权限控制。
- 推荐回答：`/api/admin/**` 在 `SecurityConfig` 中要求 ADMIN，普通 USER 无法访问。
- 不要怎么回答：不要只靠前端隐藏菜单。
- 对应代码位置：`SecurityConfig.java`。

### 25. SecurityUser 有什么作用？
- 面试官想考什么：UserDetails 理解。
- 推荐回答：封装用户 id、用户名、密码、角色，提供给 Spring Security 做认证和授权。
- 不要怎么回答：不要直接把 Entity 暴露给安全框架。
- 对应代码位置：`SecurityUser.java`。

## 订单 / 事务 / 业务设计类

### 26. 创建订单有哪些校验？
- 面试官想考什么：业务规则。
- 推荐回答：校验商品存在、状态为 `ON_SALE`，并禁止购买自己的商品。
- 不要怎么回答：不要只插入订单。
- 对应代码位置：`OrderServiceImpl#createOrder`。

### 27. 支付后发生什么？
- 面试官想考什么：状态流转。
- 推荐回答：订单改为 `PAID`，商品改为 `SOLD`，并发送订单事件。
- 不要怎么回答：不要接真实支付，本项目只是状态模拟。
- 对应代码位置：`OrderServiceImpl#payOrder`。

### 28. 取消订单怎么处理？
- 面试官想考什么：回滚业务状态。
- 推荐回答：未支付订单取消后，订单改为 `CANCELED`，商品恢复 `ON_SALE`。
- 不要怎么回答：不要取消后仍锁定商品。
- 对应代码位置：`OrderServiceImpl#cancelOrder`。

### 29. RabbitMQ 在订单里做什么？
- 面试官想考什么：异步解耦。
- 推荐回答：订单状态变化后发送事件，消费者保存到 `market_order_event_log`，主流程发送失败时记录异常但不影响订单状态。
- 不要怎么回答：不要说 RabbitMQ 参与事务扣款。
- 对应代码位置：`OrderServiceImpl.java`，`OrderEventConsumer.java`。

### 30. CI 做了什么？
- 面试官想考什么：基础工程化。
- 推荐回答：GitHub Actions 分别跑 Maven compile 和前端 npm build，作为提交后的基础验证。
- 不要怎么回答：不要说实现了完整发布系统。
- 对应代码位置：`.github/workflows/ci.yml`。
