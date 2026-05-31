# 09 安全设计

## 安全目标

1. 保证登录用户身份可信。
2. 普通用户只能操作自己的资源。
3. 管理员接口只能由 `ADMIN` 访问。
4. 防止用户通过前端传入敏感字段越权。
5. 不返回密码等敏感字段。
6. 使用简单、可讲清楚的安全实现，适合实习项目。

## JWT 认证

登录成功后返回 JWT，前端请求时携带：

```text
Authorization: Bearer <token>
```

JWT 建议包含：

| 字段 | 说明 |
| --- | --- |
| userId | 用户 ID |
| username | 用户名 |
| role | `USER` 或 `ADMIN` |
| exp | 过期时间 |

后端流程：

1. `JwtAuthenticationFilter` 从请求头读取 Token。
2. 校验签名和过期时间。
3. 查询用户状态，禁用用户不能继续访问。
4. 构造 `SecurityUser` 写入 `SecurityContext`。

## BCrypt 密码

1. 注册时使用 `BCryptPasswordEncoder` 加密。
2. 登录时使用 `PasswordEncoder.matches` 校验。
3. 数据库不保存明文密码。
4. 接口返回永远不包含 `password` 字段。

## 接口权限

| 路径 | 权限 |
| --- | --- |
| `/api/auth/login` | 放行 |
| `/api/auth/register` | 放行 |
| `/api/health` | 放行 |
| `/api/categories` | 放行 |
| `GET /api/goods/page` | 放行 |
| `GET /api/goods/{id}` | 放行 |
| `/swagger-ui/**`、`/v3/api-docs/**` | 开发环境放行 |
| `/api/admin/**` | 仅 `ADMIN` |
| 其他 `/api/**` | 登录用户 |

## 资源归属校验

必须在 Service 层做，不能只靠前端或 Controller。

| 场景 | 校验规则 |
| --- | --- |
| 修改商品 | 当前用户必须是商品卖家 |
| 下架商品 | 当前用户必须是商品卖家，管理员接口除外 |
| 创建订单 | 当前用户不能是商品卖家 |
| 查看订单 | 当前用户必须是买家、卖家或管理员 |
| 取消订单 | 当前用户必须是买家，且订单为 `CREATED` |
| 模拟支付 | 当前用户必须是买家，且订单为 `CREATED` |
| 完成订单 | 当前用户必须参与订单，且订单为 `PAID` |
| 发送消息 | 当前用户必须与商品或订单有关联 |
| 提交评价 | 当前用户必须是订单买家且订单 `FINISHED` |
| 查看举报 | 普通用户只能看自己的举报，管理员可看全部 |

## 不信任前端传参原则

以下字段不能直接信任前端：

| 字段 | 后端来源 |
| --- | --- |
| buyerId | 当前登录用户 |
| sellerId | 商品表中的卖家 ID |
| role | JWT + 数据库用户角色 |
| amount | 商品表价格 |
| goodsStatus | 后端状态机控制 |

## 管理员权限

项目只保留两类角色：

1. `USER`
2. `ADMIN`

`/api/admin/**` 必须通过 Spring Security 的 `hasRole("ADMIN")` 或等价配置限制。

## 参数校验

使用 `@Valid` 和 Validation 注解：

| 字段 | 校验 |
| --- | --- |
| username | 非空，长度限制 |
| password | 非空，长度至少 6 |
| price | 大于 0 |
| title | 非空，长度限制 |
| imageUrls | 发布商品时至少一张图 |
| rating | 1 到 5 |

## 敏感信息处理

接口返回不包含：

1. `password`
2. JWT 签名密钥
3. 管理员内部备注，除非后台接口需要
4. 用户手机号可按需部分脱敏，第一版可完整展示给交易双方
