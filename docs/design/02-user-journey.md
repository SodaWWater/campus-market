# 02 用户旅程

## 1. 买家购买流程

```mermaid
sequenceDiagram
  actor Visitor as 游客/买家
  participant Web as 前端页面
  participant Goods as 商品服务
  participant Order as 订单服务
  participant Payment as 模拟支付服务
  participant DB as MySQL

  Visitor->>Web: 浏览商品列表和详情
  Web->>Goods: GET /api/goods/page, GET /api/goods/{id}
  Goods-->>Web: 返回 ON_SALE 商品
  Visitor->>Web: 登录后点击立即购买
  Web->>Order: POST /api/orders(goodsId)
  Order->>DB: 条件更新商品 ON_SALE -> LOCKED
  Order->>DB: 创建 CREATED 订单
  Order-->>Web: 返回订单信息
  Visitor->>Web: 点击模拟支付
  Web->>Payment: POST /api/payments/mock-pay(orderId)
  Payment->>DB: 写 payment_record，订单 PAID，商品 SOLD
  Payment-->>Web: 返回支付成功
  Visitor->>Web: 线下取货后确认完成
  Web->>Order: PUT /api/orders/{id}/finish
  Order->>DB: 订单 FINISHED
  Visitor->>Web: 提交评价
```

关键规则：

1. 游客可以浏览商品，但下单前必须登录。
2. 买家不能购买自己发布的商品。
3. 只有 `ON_SALE` 商品可以创建订单。
4. 创建订单成功后商品状态变为 `LOCKED`。
5. 模拟支付成功后订单变为 `PAID`，商品变为 `SOLD`。
6. 只有 `FINISHED` 订单可以评价。

## 2. 卖家发布流程

```mermaid
flowchart TD
  A[卖家登录] --> B[完善资料]
  B --> C[填写商品信息]
  C --> D[上传商品图片]
  D --> E{保存草稿还是提交审核}
  E -->|保存草稿| F[DRAFT]
  E -->|提交审核| G[PENDING_AUDIT]
  G --> H[管理员审核]
  H -->|通过| I[ON_SALE]
  H -->|驳回| J[REJECTED]
  J --> K[卖家修改后重新提交]
  K --> G
```

卖家规则：

1. 发布时必须选择启用状态的分类。
2. 至少填写标题、描述、价格、交易地点和一张图片。
3. 审核通过前商品不出现在普通商品列表。
4. 商品被锁定、售出后不能编辑核心信息。
5. 审核驳回后可以修改并重新提交审核。

## 3. 管理员审核流程

```mermaid
flowchart TD
  A[管理员进入待审核列表] --> B[查看商品详情和图片]
  B --> C{内容是否合规}
  C -->|合规| D[商品状态改为 ON_SALE]
  C -->|不合规| E[商品状态改为 REJECTED 并填写原因]
  D --> F[写入 goods_audit_log]
  E --> F
  F --> G[写入 admin_operation_log]
```

审核规则：

1. 只有 `PENDING_AUDIT` 商品可以审核。
2. 审核通过后状态改为 `ON_SALE`。
3. 审核驳回后状态改为 `REJECTED`，必须保存原因。
4. 每次审核必须写入审核日志和管理员操作日志。

## 4. 订单取消流程

```mermaid
flowchart TD
  A[订单 CREATED 商品 LOCKED] --> B{买家取消或超时取消}
  B --> C[订单 CANCELED]
  C --> D[商品恢复 ON_SALE]
  D --> E[支付记录 WAITING 则关闭为 CLOSED]
  E --> F[写入 order_log]
```

取消规则：

1. 只有 `CREATED` 订单可以取消。
2. 取消后商品恢复 `ON_SALE`。
3. 已支付订单不能直接取消，第一版不做退款流程。

## 5. 举报处理流程

```mermaid
flowchart TD
  A[用户提交举报] --> B[举报状态 PENDING]
  B --> C[管理员查看举报]
  C --> D{举报是否成立}
  D -->|成立| E[状态 RESOLVED]
  E --> F[可联动下架商品/禁用用户]
  D -->|不成立| G[状态 REJECTED]
  F --> H[写入 admin_operation_log]
  G --> H
```

举报规则：

1. 举报对象支持商品、用户、消息、订单。
2. 管理员处理时必须填写处理结果。
3. 管理员处理动作需要留痕。
