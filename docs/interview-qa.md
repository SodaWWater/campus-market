# campus-market 面试问答

每题按"考点 · 推荐回答 · 不要 · 代码位置"准备。

---

## 项目介绍

### 1. 这个项目是什么？
- **考点**：项目定位
- **回答**：个人学习、复现与二次开发项目，校园二手交易平台。含用户端（商品浏览、下单、支付、消息、评价）和后台管理（审核、看板、操作日志）。
- **不要**：说成公司真实业务、高并发、微服务
- **代码位置**：`README.md`

### 2. 核心业务链路是什么？
- **考点**：业务理解
- **回答**：卖家发布商品 → 管理员审核通过 → 买家下单锁定商品 → 模拟支付 → 商品售出 → 买家确认完成 → 评价。取消未支付订单会恢复商品在售。
- **不要**：只说增删改查
- **代码位置**：`OrderServiceImpl.java`、`PaymentServiceImpl.java`

### 3. 前端做了什么？
- **考点**：全栈能力
- **回答**：Vue 3 + Element Plus + Vue Router，用户端和后台端独立布局。19 条路由 + 导航守卫，Axios 自动注入 JWT，401 跳转登录。
- **不要**：只说写了几个页面
- **代码位置**：`frontend/src/`

### 4. 数据库有哪些表？为什么这样设计？
- **考点**：数据库设计能力
- **回答**：13 张表。用户/商品/订单为核心实体，goods_image 支持多图，payment_record + order_log + goods_audit_log 记录状态变更留痕，favorite/message/review/report 支撑用户互动，admin_operation_log 记录管理员操作。字段用下划线，状态用 VARCHAR 便于与 Java 枚举映射。
- **不要**：只列表名不说设计理由
- **代码位置**：`scripts/init.sql`、`docs/design/05-database-design.md`

### 5. 为什么不做物理删除？
- **考点**：数据安全
- **回答**：商品用 OFF_SHELF 下架、用户用 DISABLED 禁用、分类用 DISABLED。保留数据方便审计和恢复。
- **不要**：说"忘了做删除"
- **代码位置**：`AdminServiceImpl.java`、`CategoryServiceImpl.java`

---

## 商品与审核

### 6. 商品状态有哪些？审核流程是什么？
- **考点**：状态机设计
- **回答**：DRAFT → PENDING_AUDIT → ON_SALE → LOCKED → SOLD，分支包括 REJECTED（驳回可重提）、OFF_SHELF（主动下架）。审核由 ADMIN 操作，结果写入 goods_audit_log。
- **不要**：把审核状态和交易状态混在一个字段
- **代码位置**：`docs/design/04-state-machine.md`、`GoodsServiceImpl.java`

### 7. 发布商品时 sellerId 从哪里来？
- **考点**：安全意识
- **回答**：从 SecurityContext 当前登录用户获取，不允许前端传 sellerId。防止用户冒充他人发布商品。
- **不要**：说从前端传过来也可以
- **代码位置**：`GoodsServiceImpl#createGoods`、`SecurityContextUtil.java`

### 8. 商品图片怎么处理？
- **考点**：文件上传
- **回答**：`POST /api/files/upload` 接收 multipart 文件，存储到本地 `uploads/` 目录。图片记录写入 market_goods_image 表，支持多图。
- **不要**：说没用图片功能
- **代码位置**：`FileController.java`、`WebMvcConfig.java`

---

## 订单与支付

### 9. 创建订单时如何防止并发重复下单？
- **考点**：并发控制
- **回答**：使用带状态条件的 UPDATE——`WHERE id = ? AND status = 'ON_SALE'`，影响行数为 1 才继续创建订单。与订单插入在同一个事务中，避免"先查后改"的竞态问题。
- **不要**：说先查状态再更新
- **代码位置**：`OrderServiceImpl#createOrder`

### 10. 订单状态有哪些？
- **考点**：业务流转
- **回答**：CREATED → PAID → FINISHED，分支：CREATED → CANCELED。终态不可再变更。
- **不要**：把商品状态和订单状态混淆
- **代码位置**：`MarketOrder.java`、`OrderServiceImpl.java`

### 11. 模拟支付做了什么？
- **考点**：支付建模
- **回答**：生成 payment_record（pay_no、金额、MOCK 类型、SUCCESS 状态）、订单变 PAID、商品变 SOLD、写 order_log。三者在事务中完成。
- **不要**：说集成了微信/支付宝
- **代码位置**：`PaymentServiceImpl.java`

### 12. 取消订单怎么恢复商品？
- **考点**：状态回滚
- **回答**：仅 CREATED 状态可取消。取消后订单变 CANCELED，商品恢复 ON_SALE，payment_record 变 CLOSED。
- **不要**：说取消后商品一直锁定
- **代码位置**：`OrderServiceImpl#cancelOrder`

### 13. 为什么订单日志不用 RabbitMQ？
- **考点**：架构决策
- **回答**：本项目是学习项目，订单日志、支付记录、审核日志都直接在事务中写数据库，简单可靠。如果后续需要异步通知（短信、推送），可以再引入消息队列。
- **不要**：说技术栈不够或忘了加
- **代码位置**：`OrderServiceImpl.java`、`PaymentServiceImpl.java`

---

## Redis 缓存

### 14. 分类缓存怎么做？
- **考点**：缓存基本模式
- **回答**：key 为 `market:category:list`，先查 Redis，没有再查 MySQL 并写回。新增/修改/启用/禁用分类后删除缓存。TTL 可配置。
- **不要**：说更新数据库后忘记删缓存
- **代码位置**：`CategoryServiceImpl.java`

### 15. Redis 不可用怎么办？
- **考点**：降级设计
- **回答**：所有 Redis 操作外层 try-catch，异常时直接查 MySQL 或跳过。Redis 是优化，不是必要条件。
- **不要**：说 Redis 挂了系统就挂了
- **代码位置**：`CategoryServiceImpl.java`、`GoodsServiceImpl.java`

### 16. 为什么用删除缓存而不是更新缓存？
- **考点**：缓存一致性
- **回答**：删除缓存简单可靠，下次查询自动回源 MySQL。更新缓存需要同时更新数据和缓存，容易写错或造成不一致。
- **代码位置**：`CategoryServiceImpl#evictCache`

---

## JWT / Spring Security

### 17. Spring Security 6 怎么配？（不是 WebSecurityConfigurerAdapter）
- **考点**：版本认知
- **回答**：使用 `SecurityFilterChain` Bean，配置 CSRF 关闭、无状态 session、放行公开接口、`/api/admin/**` 要求 ADMIN 角色。
- **不要**：写 WebSecurityConfigurerAdapter
- **代码位置**：`SecurityConfig.java`

### 18. JWT 认证链路是怎样的？
- **考点**：认证流程
- **回答**：`JwtAuthenticationFilter` 继承 `OncePerRequestFilter`，从请求头解析 Bearer token，验证签名和有效期，构造 SecurityUser 写入 SecurityContext。
- **不要**：说在每个 Controller 手动解析 token
- **代码位置**：`JwtAuthenticationFilter.java`

### 19. 密码怎么存储？
- **考点**：安全基础
- **回答**：注册时 BCrypt 加密，登录时 `PasswordEncoder.matches()` 校验。数据库只存 hash，样例 SQL 也用 BCrypt hash。
- **不要**：说明文或 MD5
- **代码位置**：`AuthServiceImpl.java`、`scripts/sample-data.sql`

### 20. 不同角色的接口权限怎么区分？
- **考点**：权限控制
- **回答**：SecurityConfig 中 `/api/admin/**` 限制 ADMIN 角色。前端路由守卫 meta.admin 阻止非 ADMIN 访问后台页面。前后端双重保护。
- **不要**：说只靠前端隐藏按钮
- **代码位置**：`SecurityConfig.java`、`frontend/src/router/index.js`

---

## 用户互动（V3）

### 21. 收藏功能怎么保证计数正确？
- **考点**：数据一致性
- **回答**：收藏/取消收藏与 market_goods 的 favorite_count 增减在同一事务中，使用唯一索引 uk_user_goods 防止重复收藏。
- **代码位置**：`FavoriteServiceImpl.java`

### 22. 站内消息的会话列表怎么实现？
- **考点**：查询设计
- **回答**：查询当前用户参与的所有消息，按对方用户分组，取最后一条消息内容、时间和未读数。支持标记已读。
- **代码位置**：`MessageServiceImpl.java`

### 23. 评价有什么业务规则？
- **考点**：业务约束
- **回答**：只有 FINISHED 订单可以评价，只有买家可以评价，一个订单只能评价一次（order_id UNIQUE）。评价公开可查。
- **代码位置**：`ReviewServiceImpl.java`

### 24. 举报的处理流程是什么？
- **考点**：工作流
- **回答**：用户提交 → PENDING → 管理员处理 → RESOLVED/REJECTED。处理结果和操作人记录在 market_report 表中。
- **代码位置**：`ReportServiceImpl.java`、`AdminController.java`

---

## 工程化

### 25. Docker Compose 做了什么？
- **考点**：环境管理
- **回答**：一键启动 MySQL 8.0 + Redis 7。SQL 初始化脚本自动建表插入样例数据。应用配置的默认端口与容器映射一致。
- **代码位置**：`docker-compose.yml`、`scripts/`

### 26. CI 做了什么？
- **考点**：自动化
- **回答**：GitHub Actions 在每次 push 时分别编译后端（mvn compile）和前端（npm build），作为提交后的基础验证。
- **代码位置**：`.github/workflows/ci.yml`

### 27. 统一返回结构是什么？
- **考点**：接口规范
- **回答**：`Result<T>` 包装 code、message、data。异常由 GlobalExceptionHandler 统一处理，返回标准错误码。
- **代码位置**：`common/Result.java`、`GlobalExceptionHandler.java`

### 28. 参数校验怎么做？
- **考点**：输入校验
- **回答**：DTO 使用 `@NotNull`、`@NotBlank`、`@Min`、`@Max` 等 Jakarta Validation 注解，Controller 用 `@Valid` 触发校验。
- **代码位置**：`dto/` 包

### 29. MyBatis-Plus 在本项目的作用？
- **考点**：技术选型
- **回答**：继承 `ServiceImpl` 获得 CRUD 能力，LambdaQueryWrapper 做条件查询避免字符串拼 SQL，分页用 IPage。减少样板代码同时保持灵活性。
- **代码位置**：各 ServiceImpl

### 30. 这个项目你后续会怎么改进？
- **考点**：技术规划
- **回答**：(1) 引入 WebSocket 做实时消息推送 (2) 订单超时自动取消 (3) 浏览量异步回写 (4) 商品搜索用 Elasticsearch (5) 接入 MinIO 做图片存储
- **不要**：说当前项目已经很完善不需要改进
- **代码位置**：`docs/design/13-development-roadmap.md`
