# 07 后端架构设计

## 架构定位

项目采用 **Spring Boot 单体模块化架构**。不做微服务，不引入注册中心、网关、消息队列和分布式事务。所有核心业务通过 Service 层事务、数据库状态和操作日志完成。

## 分层结构

```text
Controller → Service → Mapper → MySQL
              ↓
            Redis
              ↓
          Local File Storage
```

- Controller：接收请求、参数校验、返回统一结果。
- Service：业务规则、事务、状态流转、资源归属校验。
- Mapper：MyBatis-Plus 数据访问。
- Redis：缓存分类、热门商品、浏览量、未读消息数。
- Local File Storage：V1 本地保存商品图片。

## 推荐包结构

```text
com.liminghan.market
├── auth
│   ├── controller
│   ├── dto
│   ├── service
│   └── vo
├── user
├── category
├── goods
├── favorite
├── order
├── payment
├── message
├── review
├── report
├── admin
├── file
├── common
│   ├── Result.java
│   ├── ErrorCode.java
│   ├── BusinessException.java
│   └── GlobalExceptionHandler.java
├── config
└── security
```

如果当前代码仍是 `controller/service/entity/mapper` 传统分层，可以先不强制重构；后续 V2/V3 再逐步迁移到业务模块分包。

## 核心组件

| 组件 | 说明 |
| --- | --- |
| `SecurityConfig` | Spring Security 6 配置，使用 `SecurityFilterChain` |
| `JwtAuthenticationFilter` | 解析 `Authorization: Bearer <token>` |
| `SecurityContextUtil` | 获取当前登录用户 ID 和角色 |
| `GoodsService` | 商品发布、审核状态、列表详情、上下架 |
| `OrderService` | 创建订单、取消订单、完成订单、事务控制 |
| `PaymentService` | 模拟支付、支付记录 |
| `AdminService` | 后台看板、审核、举报、用户管理 |
| `FileService` | 本地图片上传和访问路径生成 |

## 订单创建事务设计

创建订单必须在一个事务中完成：

```text
1. 获取当前买家 ID
2. 查询商品基础信息
3. 校验商品不是自己发布的
4. 使用条件更新锁定商品：ON_SALE -> LOCKED
5. 如果影响行数为 0，抛出商品不可售异常
6. 创建订单 CREATED
7. 写 order_log
8. 返回订单信息
```

关键 SQL：

```sql
UPDATE market_goods
SET status = 'LOCKED', updated_at = NOW()
WHERE id = ? AND status = 'ON_SALE';
```

该设计用于避免两个买家同时购买同一商品。

## 模拟支付事务设计

模拟支付必须在一个事务中完成：

```text
1. 校验当前用户是订单买家
2. 校验订单状态为 CREATED
3. 创建或更新 payment_record 为 SUCCESS
4. 订单状态 CREATED -> PAID
5. 商品状态 LOCKED -> SOLD
6. 写 order_log
```

## 商品审核设计

```text
1. 管理员查询 PENDING_AUDIT 商品
2. 审核通过：goods.status = ON_SALE
3. 审核驳回：goods.status = REJECTED，goods.audit_reason = 原因
4. 写 goods_audit_log
5. 写 admin_operation_log
```

## 文件上传设计

V1 使用本地目录：

```text
uploads/goods/yyyyMMdd/xxx.jpg
```

访问路径：

```text
/uploads/goods/yyyyMMdd/xxx.jpg
```

第一版不引入 MinIO，后续工程化阶段可替换。

## 日志设计

项目不引入 RabbitMQ。订单日志、审核日志、管理员操作日志在对应 Service 事务中直接写入数据库。

| 日志 | 写入时机 |
| --- | --- |
| `order_log` | 创建、支付、取消、完成订单 |
| `goods_audit_log` | 管理员审核商品 |
| `admin_operation_log` | 审核、下架、禁用用户、处理举报等后台操作 |

## 异常处理

所有业务异常抛出 `BusinessException`，由 `GlobalExceptionHandler` 统一转换为：

```json
{
  "code": 40001,
  "message": "业务错误说明",
  "data": null
}
```
