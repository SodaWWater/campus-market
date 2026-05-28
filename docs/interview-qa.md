# 面试问答

## 项目介绍类

1. 这个项目是做什么的？
   - 面试官想考什么：项目定位。
   - 推荐回答：这是校园二手交易后端项目，包含注册登录、商品、分类、订单、后台管理。
   - 不要怎么回答：不要说是完整电商平台。
   - 对应代码位置：`AuthController`、`GoodsController`、`OrderController`

2. 项目适合投什么岗位？
   - 面试官想考什么：技术栈匹配。
   - 推荐回答：适合 Java 后端实习，因为覆盖 Spring Boot、MyBatis-Plus、MySQL、Redis、JWT、事务。
   - 不要怎么回答：不要说覆盖所有后端技术。
   - 对应代码位置：`README.md`

3. 项目核心链路是什么？
   - 面试官想考什么：业务理解。
   - 推荐回答：用户登录拿 token，发布商品，买家创建订单，订单状态和商品状态同步变化。
   - 不要怎么回答：不要只说增删改查。
   - 对应代码位置：`AuthServiceImpl`、`GoodsServiceImpl`、`OrderServiceImpl`

4. 你做了哪些模块？
   - 面试官想考什么：个人工作范围。
   - 推荐回答：认证、商品、分类、订单、后台管理、SQL 和文档。
   - 不要怎么回答：不要说自己做了复杂前端或真实支付。
   - 对应代码位置：`controller`、`service.impl`

5. 这个项目没有做什么？
   - 面试官想考什么：边界意识。
   - 推荐回答：没有做真实支付、复杂 RBAC、微服务和复杂前端。
   - 不要怎么回答：不要把未实现功能写进简历。
   - 对应代码位置：`README.md`

## Spring Boot 类

6. 项目如何分层？
   - 面试官想考什么：工程结构。
   - 推荐回答：Controller 接口层，Service 业务层，Mapper 数据访问层，Entity 映射表，DTO/VO 区分请求响应。
   - 不要怎么回答：不要把业务堆在 Controller。
   - 对应代码位置：`controller`、`service`、`mapper`

7. 统一返回怎么做？
   - 面试官想考什么：接口规范。
   - 推荐回答：所有接口返回 `Result<T>`，包含 code、message、data。
   - 不要怎么回答：不要每个接口返回不同结构。
   - 对应代码位置：`Result`

8. 异常如何处理？
   - 面试官想考什么：工程实践。
   - 推荐回答：业务异常用 `BusinessException`，统一由 `GlobalExceptionHandler` 返回。
   - 不要怎么回答：不要直接抛原始异常给前端。
   - 对应代码位置：`GlobalExceptionHandler`

9. OpenAPI 有什么用？
   - 面试官想考什么：接口调试。
   - 推荐回答：通过 springdoc-openapi 生成 Swagger 页面，便于查看和测试接口。
   - 不要怎么回答：不要说只靠口头说明接口。
   - 对应代码位置：`OpenApiConfig`

10. 为什么使用 Maven release 17？
    - 面试官想考什么：Java 版本意识。
    - 推荐回答：交易项目面向常见 Java 后端实习场景，用 release 17 更容易匹配多数 Spring Boot 3 项目环境。
    - 不要怎么回答：不要说版本随便写。
    - 对应代码位置：`pom.xml`

## MyBatis / MySQL 类

11. 数据库有哪些核心表？
    - 面试官想考什么：数据建模。
    - 推荐回答：`sys_user`、`market_category`、`market_goods`、`market_order`。
    - 不要怎么回答：不要只说用户表和商品表。
    - 对应代码位置：`scripts/init.sql`

12. MyBatis-Plus 用在哪里？
    - 面试官想考什么：ORM 使用。
    - 推荐回答：每张表都有 Entity、Mapper、Service，使用 BaseMapper 和 IService 完成常见操作。
    - 不要怎么回答：不要说所有 SQL 都手写。
    - 对应代码位置：`entity`、`mapper`、`service`

13. 字段命名如何映射？
    - 面试官想考什么：映射细节。
    - 推荐回答：SQL 用下划线，Java 用驼峰，配置 `map-underscore-to-camel-case`。
    - 不要怎么回答：不要说字段名不需要对应。
    - 对应代码位置：`application.yml`

14. 商品分页怎么实现？
    - 面试官想考什么：分页查询。
    - 推荐回答：使用 MyBatis-Plus `Page`，支持关键词 `like` 和分类 `eq` 条件。
    - 不要怎么回答：不要一次查全量再内存分页。
    - 对应代码位置：`GoodsServiceImpl`

15. 为什么不用 `user` 表名？
    - 面试官想考什么：SQL 基础。
    - 推荐回答：`user` 容易和数据库关键字或系统表混淆，所以使用 `sys_user`。
    - 不要怎么回答：不要忽略表名兼容性。
    - 对应代码位置：`scripts/init.sql`

## Redis 类

16. Redis 用在哪里？
    - 面试官想考什么：缓存场景。
    - 推荐回答：缓存分类列表，key 为 `market:category:list`。
    - 不要怎么回答：不要说所有数据都放 Redis。
    - 对应代码位置：`CategoryServiceImpl`

17. 分类缓存流程是什么？
    - 面试官想考什么：缓存读写。
    - 推荐回答：先查 Redis，未命中查 MySQL，查到后写 Redis。
    - 不要怎么回答：不要说每次都查数据库。
    - 对应代码位置：`CategoryServiceImpl`

18. 分类变更后怎么处理缓存？
    - 面试官想考什么：缓存一致性。
    - 推荐回答：新增、修改、删除分类后删除 `market:category:list`。
    - 不要怎么回答：不要让旧缓存一直存在。
    - 对应代码位置：`CategoryServiceImpl`

19. Redis 不可用怎么办？
    - 面试官想考什么：降级意识。
    - 推荐回答：Redis 操作用 try-catch 包住，不可用时直接查 MySQL，不影响主流程。
    - 不要怎么回答：不要让缓存失败导致接口不可用。
    - 对应代码位置：`CategoryServiceImpl`

20. 为什么分类适合缓存？
    - 面试官想考什么：缓存选择。
    - 推荐回答：分类读多写少，数据量小，适合缓存。
    - 不要怎么回答：不要说所有表都适合缓存。
    - 对应代码位置：`redis-usage.md`

## JWT / Spring Security 类

21. Spring Security 6 怎么配置？
    - 面试官想考什么：新版本配置方式。
    - 推荐回答：使用 `SecurityFilterChain`，配置放行路径、ADMIN 路径和其他接口登录要求。
    - 不要怎么回答：不要提 `WebSecurityConfigurerAdapter`。
    - 对应代码位置：`SecurityConfig`

22. JWT Filter 做了什么？
    - 面试官想考什么：鉴权流程。
    - 推荐回答：读取 Authorization Header，解析 Bearer token，有效时写入 SecurityContext。
    - 不要怎么回答：不要说每次都查 session。
    - 对应代码位置：`JwtAuthenticationFilter`

23. 密码如何保存？
    - 面试官想考什么：安全基础。
    - 推荐回答：注册时使用 BCrypt hash，登录时用 `PasswordEncoder.matches` 校验。
    - 不要怎么回答：不要明文保存密码。
    - 对应代码位置：`AuthServiceImpl`

24. ADMIN 接口如何限制？
    - 面试官想考什么：权限控制。
    - 推荐回答：`/api/admin/**` 在 SecurityConfig 中配置 `hasRole("ADMIN")`。
    - 不要怎么回答：不要依赖前端隐藏按钮。
    - 对应代码位置：`SecurityConfig`

25. token 中包含什么？
    - 面试官想考什么：登录态设计。
    - 推荐回答：包含 userId、username、role、exp，并使用 HMAC-SHA256 签名。
    - 不要怎么回答：不要把密码放 token 里。
    - 对应代码位置：`JwtUtil`

## 订单 / 事务 / 业务设计类

26. 创建订单校验什么？
    - 面试官想考什么：业务规则。
    - 推荐回答：校验商品存在、状态为 `ON_SALE`，且不能购买自己的商品。
    - 不要怎么回答：不要直接插入订单。
    - 对应代码位置：`OrderServiceImpl`

27. 创建订单后商品状态怎么变？
    - 面试官想考什么：状态联动。
    - 推荐回答：订单创建后商品状态从 `ON_SALE` 改为 `LOCKED`。
    - 不要怎么回答：不要让商品继续被别人买。
    - 对应代码位置：`OrderServiceImpl`

28. 支付后发生什么？
    - 面试官想考什么：状态流转。
    - 推荐回答：订单从 `CREATED` 改为 `PAID`，商品从 `LOCKED` 改为 `SOLD`。
    - 不要怎么回答：不要说接入了真实支付。
    - 对应代码位置：`OrderServiceImpl`

29. 取消订单如何处理？
    - 面试官想考什么：异常流程。
    - 推荐回答：只有未支付订单可取消，取消后商品恢复 `ON_SALE`。
    - 不要怎么回答：不要允许任意状态取消。
    - 对应代码位置：`OrderServiceImpl`

30. 为什么订单操作要事务？
    - 面试官想考什么：一致性。
    - 推荐回答：订单和商品状态要一起修改，事务保证失败时一起回滚。
    - 不要怎么回答：不要说事务只是注解装饰。
    - 对应代码位置：`OrderServiceImpl`
