# 数据库设计

数据库名：`campus_market`，字符集 `utf8mb4`。

## 设计约定

1. 主键统一 `BIGINT AUTO_INCREMENT`
2. 时间字段统一 `DATETIME`
3. 金额字段统一 `DECIMAL(10,2)`
4. 状态字段 `VARCHAR(32)`，便于 Java 枚举映射
5. 字段名用下划线，Java Entity 用驼峰
6. 删除优先用状态禁用/下架，不做物理删除

## 表清单（13 张）

| 表名 | 说明 | 核心字段 |
|------|------|----------|
| `sys_user` | 用户 | username, password(BCrypt), role, status, school, major, grade, bio, credit_score |
| `market_category` | 分类 | name(UNIQUE), sort, status |
| `market_goods` | 商品 | seller_id, category_id, title, price, condition_level, status(7-state), cover_image, audit_reason |
| `market_goods_image` | 商品图片 | goods_id, image_url, sort |
| `market_order` | 订单 | order_no(UNIQUE), buyer_id, seller_id, goods_id, amount, status |
| `payment_record` | 支付记录 | pay_no(UNIQUE), order_id, amount, pay_type, pay_status, paid_at |
| `order_log` | 订单日志 | order_id, operator_id, action, from_status, to_status |
| `goods_audit_log` | 商品审核日志 | goods_id, admin_id, result(APPROVED/REJECTED), reason |
| `market_favorite` | 收藏 | user_id + goods_id(UNIQUE) |
| `market_message` | 站内消息 | sender_id, receiver_id, content, read_status |
| `market_review` | 评价 | order_id(UNIQUE), rating(1-5), content |
| `market_report` | 举报 | target_type, target_id, reason, status, handler_id, handle_result |
| `admin_operation_log` | 管理员操作日志 | admin_id, module, operation, target_id, content |

## 状态机

### 商品
`DRAFT → PENDING_AUDIT → ON_SALE → LOCKED → SOLD`
分支：`PENDING_AUDIT → REJECTED → PENDING_AUDIT`、`ON_SALE → OFF_SHELF`、`LOCKED → ON_SALE`

### 订单
`CREATED → PAID → FINISHED`、`CREATED → CANCELED`

### 支付
`WAITING → SUCCESS / FAILED / CLOSED`

### 举报
`PENDING → PROCESSING → RESOLVED / REJECTED`

## 索引

- 唯一：username、category.name、order_no、pay_no、favorite(user,goods)、review(order)
- 普通：role、status(多表)、seller_id、buyer_id、goods_id、category_id、admin_id、sender_id、receiver_id

详细设计见 `docs/design/05-database-design.md`
