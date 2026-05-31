# campus-market API 文档

统一返回：`{"code":0,"message":"success","data":{}}`

鉴权：除标注"公开"的接口外，都需要 `Authorization: Bearer <token>`

## 公开接口

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/health` | 健康检查 |
| POST | `/api/auth/register` | 注册 |
| POST | `/api/auth/login` | 登录 |
| GET | `/api/categories` | 启用分类列表 |
| GET | `/api/goods/page` | 商品分页（仅 ON_SALE） |
| GET | `/api/goods/{id}` | 商品详情 |
| GET | `/api/reviews/goods/{goodsId}` | 商品评价 |
| GET | `/api/reviews/users/{userId}` | 用户评价 |

## 认证

### 注册 `POST /api/auth/register`
```json
{"username":"student1","password":"123456","nickname":"学生一","phone":"13800000000"}
```
返回：`{token, userId, username, role}`

### 登录 `POST /api/auth/login`
```json
{"username":"student1","password":"123456"}
```
返回：`{token, userId, username, role}`

## 用户

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/users/profile` | 当前用户资料 |
| PUT | `/api/users/profile` | 修改资料 |
| GET | `/api/users/{id}` | 用户公开主页 |

## 文件

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/files/upload` | 上传文件（multipart） |

## 商品

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/goods/page` | 商品分页（公开，仅 ON_SALE） |
| GET | `/api/goods/{id}` | 商品详情（公开） |
| POST | `/api/goods` | 发布商品 → PENDING_AUDIT |
| PUT | `/api/goods/{id}` | 修改自己的商品 |
| PUT | `/api/goods/{id}/submit` | 提交审核 |
| PUT | `/api/goods/{id}/off` | 卖家下架自己的商品 |
| GET | `/api/goods/my` | 我的商品 |
| POST | `/api/goods/{id}/favorite` | 收藏 |
| DELETE | `/api/goods/{id}/favorite` | 取消收藏 |

## 收藏

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/favorites/my` | 我的收藏列表 |

## 订单

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/orders` | 创建订单 `{"goodsId":1}` |
| GET | `/api/orders/my-buy` | 我买到的 |
| GET | `/api/orders/my-sell` | 我卖出的 |
| GET | `/api/orders/{id}` | 订单详情 |
| PUT | `/api/orders/{id}/cancel` | 取消（仅 CREATED） |
| PUT | `/api/orders/{id}/finish` | 确认完成（仅 PAID） |

## 支付

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/payments/mock-pay` | 模拟支付 `{"orderId":1}` |
| GET | `/api/payments/order/{orderId}` | 查询订单支付记录 |

## 消息

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/messages` | 发送消息 |
| GET | `/api/messages/conversations` | 会话列表 |
| GET | `/api/messages/conversations/{userId}` | 与某用户的聊天记录 |
| GET | `/api/messages/unread-count` | 未读数量 |
| PUT | `/api/messages/{id}/read` | 标记已读 |

## 评价

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/reviews` | 提交评价（仅 FINISHED 订单买家） |
| GET | `/api/reviews/goods/{goodsId}` | 商品评价（公开） |
| GET | `/api/reviews/users/{userId}` | 用户评价（公开） |

## 举报

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/reports` | 提交举报 |
| GET | `/api/reports/my` | 我的举报 |

## 后台管理（需 ADMIN）

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | `/api/admin/dashboard` | 数据看板 |
| GET | `/api/admin/users` | 用户列表 |
| PUT | `/api/admin/users/{id}/disable` | 禁用用户 |
| PUT | `/api/admin/users/{id}/enable` | 启用用户 |
| GET | `/api/admin/goods` | 商品管理列表 |
| GET | `/api/admin/goods/pending` | 待审核商品 |
| PUT | `/api/admin/goods/{id}/approve` | 审核通过 |
| PUT | `/api/admin/goods/{id}/reject` | 审核驳回 `{"reason":"原因"}` |
| PUT | `/api/admin/goods/{id}/off` | 管理员下架 |
| GET | `/api/admin/orders` | 订单列表 |
| POST | `/api/admin/categories` | 新增分类 |
| PUT | `/api/admin/categories/{id}` | 修改分类 |
| PUT | `/api/admin/categories/{id}/enable` | 启用分类 |
| PUT | `/api/admin/categories/{id}/disable` | 禁用分类 |
| DELETE | `/api/admin/categories/{id}` | 删除分类 |
| GET | `/api/admin/reports` | 举报列表 |
| PUT | `/api/admin/reports/{id}/handle` | 处理举报 |
| GET | `/api/admin/operation-logs` | 操作日志 |

## 响应码

| code | 含义 |
|------|------|
| 0 | 成功 |
| 40001 | 参数错误 |
| 40100 | 未登录 |
| 40300 | 无权限 |
| 40400 | 资源不存在 |
| 50000 | 服务器错误 |
