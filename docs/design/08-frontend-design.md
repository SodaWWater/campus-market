# 08 前端设计

## 前端定位

前端分两阶段：

1. **V1：原生 HTML/CSS/JavaScript 页面**，用于快速演示完整交易流程，降低学习成本。
2. **V4：Vue 3 + Vite + Element Plus**，用于更接近真实商业项目的用户端和后台管理端。

不在 V1 强制引入 Vue，避免前端复杂度影响后端主线。

## V1 原生页面

建议文件：

```text
src/main/resources/static/index.html
```

页面区域：

| 区域 | 功能 |
| --- | --- |
| 登录区 | 登录、保存 token、显示当前用户角色 |
| 商品列表区 | 查询商品、关键词筛选、分类筛选 |
| 商品详情区 | 查看图片、描述、价格、卖家、状态 |
| 发布商品区 | 填写商品信息、上传图片、提交审核 |
| 我的订单区 | 查看买到的订单、支付、取消、完成 |
| 我的商品区 | 查看发布的商品和审核状态 |
| 管理员区 | 待审核商品、审核通过、审核驳回、看板 |

## V1 页面与接口映射

| 页面动作 | 调用接口 |
| --- | --- |
| 登录 | `POST /api/auth/login` |
| 注册 | `POST /api/auth/register` |
| 查询分类 | `GET /api/categories` |
| 商品列表 | `GET /api/goods/page` |
| 商品详情 | `GET /api/goods/{id}` |
| 上传图片 | `POST /api/files/upload` |
| 发布商品 | `POST /api/goods` |
| 我的商品 | `GET /api/goods/my` |
| 创建订单 | `POST /api/orders` |
| 我的订单 | `GET /api/orders/my-buy`、`GET /api/orders/my-sell` |
| 模拟支付 | `POST /api/payments/mock-pay` |
| 取消订单 | `PUT /api/orders/{id}/cancel` |
| 完成订单 | `PUT /api/orders/{id}/finish` |
| 待审核商品 | `GET /api/admin/goods/pending` |
| 审核通过 | `PUT /api/admin/goods/{id}/approve` |
| 审核驳回 | `PUT /api/admin/goods/{id}/reject` |

## V4 Vue 页面规划

### 用户端路由

```text
/login
/register
/market
/goods/:id
/seller/goods
/seller/goods/new
/buyer/orders
/seller/orders
/favorites
/messages
/profile
```

### 后台端路由

```text
/admin/dashboard
/admin/categories
/admin/goods
/admin/goods/pending
/admin/orders
/admin/users
/admin/reports
/admin/logs
```

## 页面说明

### 商品广场 `/market`

展示字段：封面图、标题、价格、分类、成色、交易地点、收藏数。

动作：搜索、分类筛选、查看详情、收藏。

### 商品详情 `/goods/:id`

展示字段：多图、标题、描述、价格、卖家信息、浏览量、收藏数。

动作：收藏、联系卖家、立即购买、举报。

### 发布商品 `/seller/goods/new`

字段：分类、标题、描述、价格、成色、交易地点、图片。

动作：上传图片、提交审核。

### 我的订单 `/buyer/orders`

展示：订单号、商品、金额、状态、卖家、创建时间。

动作：支付、取消、完成、评价。

### 后台审核 `/admin/goods/pending`

展示：待审核商品、图片、卖家、价格、描述。

动作：审核通过、审核驳回。

## 前端鉴权

1. 登录成功后保存 token。
2. 请求拦截器统一添加 `Authorization: Bearer <token>`。
3. 401 时清空登录态并跳转登录页。
4. ADMIN 页面前端隐藏入口，但真正权限以后端为准。
