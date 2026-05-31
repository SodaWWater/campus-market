# 06 API 设计

## 通用返回结构

```json
{
  "code": 0,
  "message": "success",
  "data": {}
}
```

错误示例：

```json
{
  "code": 40001,
  "message": "商品状态不允许创建订单",
  "data": null
}
```

## 路径风格

所有接口统一使用复数 REST 风格：

```text
/api/auth
/api/users
/api/files
/api/categories
/api/goods
/api/favorites
/api/orders
/api/payments
/api/messages
/api/reviews
/api/reports
/api/admin
```

## 公开接口

以下接口不需要登录：

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| POST | `/api/auth/register` | 注册 |
| POST | `/api/auth/login` | 登录 |
| GET | `/api/health` | 健康检查 |
| GET | `/api/categories` | 查询启用分类 |
| GET | `/api/goods/page` | 商品列表 |
| GET | `/api/goods/{id}` | 商品详情 |
| GET | `/swagger-ui.html` | Swagger 页面 |

## 认证模块 `/api/auth`

### 注册

`POST /api/auth/register`

请求：

```json
{
  "username": "student1",
  "password": "123456",
  "nickname": "学生一",
  "phone": "13800000000"
}
```

返回：登录结果，包含 token、userId、username、role。

### 登录

`POST /api/auth/login`

请求：

```json
{
  "username": "student1",
  "password": "123456"
}
```

## 用户模块 `/api/users`

| 方法 | 路径 | 登录 | 说明 |
| --- | --- | --- | --- |
| GET | `/api/users/profile` | 是 | 当前用户资料 |
| PUT | `/api/users/profile` | 是 | 修改当前用户资料 |
| GET | `/api/users/{id}` | 否 | 用户公开主页 |

修改资料请求：

```json
{
  "nickname": "小李",
  "phone": "13800000000",
  "avatarUrl": "/uploads/avatar/a.jpg",
  "school": "天津理工大学",
  "major": "软件工程",
  "grade": "2023",
  "bio": "喜欢 Java 后端和 AI 应用开发"
}
```

## 文件模块 `/api/files`

### 上传文件

`POST /api/files/upload`

- 登录：是
- Content-Type：`multipart/form-data`
- 参数：`file`
- 第一版存储到本地 `uploads/`，不引入 MinIO。

返回：

```json
{
  "code": 0,
  "message": "success",
  "data": {
    "url": "/uploads/goods/20260531/abc.jpg"
  }
}
```

## 分类模块 `/api/categories`

| 方法 | 路径 | 登录 | 角色 | 说明 |
| --- | --- | --- | --- | --- |
| GET | `/api/categories` | 否 | - | 查询启用分类 |
| POST | `/api/admin/categories` | 是 | ADMIN | 新增分类 |
| PUT | `/api/admin/categories/{id}` | 是 | ADMIN | 修改分类 |
| PUT | `/api/admin/categories/{id}/enable` | 是 | ADMIN | 启用分类 |
| PUT | `/api/admin/categories/{id}/disable` | 是 | ADMIN | 禁用分类 |

## 商品模块 `/api/goods`

| 方法 | 路径 | 登录 | 说明 |
| --- | --- | --- | --- |
| GET | `/api/goods/page` | 否 | 商品列表，默认只查 `ON_SALE` |
| GET | `/api/goods/{id}` | 否 | 商品详情 |
| POST | `/api/goods` | 是 | 发布商品，默认进入 `PENDING_AUDIT` |
| PUT | `/api/goods/{id}` | 是 | 修改自己的商品，必要时重新审核 |
| PUT | `/api/goods/{id}/submit` | 是 | 草稿提交审核 |
| PUT | `/api/goods/{id}/off` | 是 | 卖家下架自己的商品 |
| GET | `/api/goods/my` | 是 | 我的商品 |

发布商品请求：

```json
{
  "categoryId": 1,
  "title": "九成新机械键盘",
  "description": "自用一年，无故障",
  "price": 99.00,
  "conditionLevel": "GOOD",
  "tradeLocation": "图书馆门口",
  "imageUrls": ["/uploads/goods/a.jpg", "/uploads/goods/b.jpg"]
}
```

列表查询参数：

```text
keyword, categoryId, minPrice, maxPrice, conditionLevel, pageNum, pageSize
```

## 收藏模块 `/api/favorites`

| 方法 | 路径 | 登录 | 说明 |
| --- | --- | --- | --- |
| POST | `/api/goods/{id}/favorite` | 是 | 收藏商品 |
| DELETE | `/api/goods/{id}/favorite` | 是 | 取消收藏 |
| GET | `/api/favorites/my` | 是 | 我的收藏 |

## 订单模块 `/api/orders`

| 方法 | 路径 | 登录 | 说明 |
| --- | --- | --- | --- |
| POST | `/api/orders` | 是 | 创建订单 |
| GET | `/api/orders/my-buy` | 是 | 我买到的订单 |
| GET | `/api/orders/my-sell` | 是 | 我卖出的订单 |
| GET | `/api/orders/{id}` | 是 | 订单详情 |
| PUT | `/api/orders/{id}/cancel` | 是 | 取消未支付订单 |
| PUT | `/api/orders/{id}/finish` | 是 | 确认完成 |

创建订单请求：

```json
{
  "goodsId": 1001
}
```

规则：买家 ID 从 JWT 中取，金额以后端商品价格为准，不允许前端传 `buyerId` 或 `amount`。

## 支付模块 `/api/payments`

| 方法 | 路径 | 登录 | 说明 |
| --- | --- | --- | --- |
| POST | `/api/payments/mock-pay` | 是 | 模拟支付 |
| GET | `/api/payments/order/{orderId}` | 是 | 查询订单支付记录 |

模拟支付请求：

```json
{
  "orderId": 2001
}
```

## 消息模块 `/api/messages`

| 方法 | 路径 | 登录 | 说明 |
| --- | --- | --- | --- |
| POST | `/api/messages` | 是 | 发送站内消息 |
| GET | `/api/messages/conversations` | 是 | 会话列表 |
| GET | `/api/messages/conversations/{userId}` | 是 | 与某用户的消息 |
| GET | `/api/messages/unread-count` | 是 | 未读数量 |
| PUT | `/api/messages/{id}/read` | 是 | 标记已读 |

## 评价模块 `/api/reviews`

| 方法 | 路径 | 登录 | 说明 |
| --- | --- | --- | --- |
| POST | `/api/reviews` | 是 | 提交评价 |
| GET | `/api/reviews/goods/{goodsId}` | 否 | 商品评价 |
| GET | `/api/reviews/users/{userId}` | 否 | 用户评价 |

规则：只有 `FINISHED` 订单可以评价，一个订单只能评价一次。

## 举报模块 `/api/reports`

| 方法 | 路径 | 登录 | 说明 |
| --- | --- | --- | --- |
| POST | `/api/reports` | 是 | 提交举报 |
| GET | `/api/reports/my` | 是 | 我的举报 |

## 后台管理 `/api/admin`

所有后台接口需要 `ADMIN`。

| 方法 | 路径 | 说明 |
| --- | --- | --- |
| GET | `/api/admin/dashboard` | 数据看板 |
| GET | `/api/admin/users` | 用户列表 |
| PUT | `/api/admin/users/{id}/disable` | 禁用用户 |
| PUT | `/api/admin/users/{id}/enable` | 启用用户 |
| GET | `/api/admin/goods` | 商品管理列表 |
| GET | `/api/admin/goods/pending` | 待审核商品 |
| PUT | `/api/admin/goods/{id}/approve` | 审核通过 |
| PUT | `/api/admin/goods/{id}/reject` | 审核驳回 |
| PUT | `/api/admin/goods/{id}/off` | 管理员下架商品 |
| GET | `/api/admin/orders` | 订单列表 |
| GET | `/api/admin/reports` | 举报列表 |
| PUT | `/api/admin/reports/{id}/handle` | 处理举报 |
| GET | `/api/admin/operation-logs` | 管理员操作日志 |
