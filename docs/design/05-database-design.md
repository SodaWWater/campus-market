# 05 数据库设计

## 设计约定

数据库名：`campus_market`

通用约定：

1. 主键统一使用 `BIGINT AUTO_INCREMENT`。
2. 时间字段统一使用 `DATETIME`。
3. 金额字段统一使用 `DECIMAL(10,2)`。
4. 状态字段使用 `VARCHAR(32)`，便于与 Java 枚举映射。
5. 字段命名使用下划线，Java Entity 使用驼峰命名。
6. 删除优先使用状态禁用或下架，不做物理删除。
7. 所有表默认包含 `created_at`，需要更新的业务表包含 `updated_at`。

## 表清单

| 表名 | 说明 | 阶段 |
| --- | --- | --- |
| `sys_user` | 用户表 | V1 |
| `market_category` | 分类表 | V1 |
| `market_goods` | 商品表 | V1 |
| `market_goods_image` | 商品图片表 | V1 |
| `market_order` | 订单表 | V1 |
| `payment_record` | 支付记录表 | V1 |
| `order_log` | 订单日志表 | V1 |
| `goods_audit_log` | 商品审核日志 | V1 |
| `market_favorite` | 收藏表 | V3 |
| `market_message` | 站内消息表 | V3 |
| `market_review` | 评价表 | V3 |
| `market_report` | 举报表 | V3 |
| `admin_operation_log` | 管理员操作日志 | V2/V3 |

## sys_user

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | BIGINT PK | 用户 ID |
| username | VARCHAR(64) | 登录账号，唯一 |
| password | VARCHAR(128) | BCrypt 密码摘要 |
| nickname | VARCHAR(64) | 昵称 |
| phone | VARCHAR(32) | 手机号 |
| avatar_url | VARCHAR(255) | 头像 URL |
| school | VARCHAR(100) | 学校 |
| major | VARCHAR(100) | 专业 |
| grade | VARCHAR(32) | 年级 |
| bio | VARCHAR(500) | 个人简介 |
| credit_score | INT | 信用分，默认 100，第一版只展示 |
| role | VARCHAR(20) | `USER`、`ADMIN` |
| status | VARCHAR(20) | `ENABLE`、`DISABLED` |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

索引：`uk_username(username)`、`idx_role(role)`、`idx_status(status)`。

## market_category

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | BIGINT PK | 分类 ID |
| name | VARCHAR(64) | 分类名称，唯一 |
| sort | INT | 排序值 |
| status | VARCHAR(20) | `ENABLE`、`DISABLED` |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

索引：`uk_category_name(name)`、`idx_status_sort(status, sort)`。

## market_goods

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | BIGINT PK | 商品 ID |
| seller_id | BIGINT | 卖家用户 ID |
| category_id | BIGINT | 分类 ID |
| title | VARCHAR(128) | 商品标题 |
| description | TEXT | 商品描述 |
| price | DECIMAL(10,2) | 商品价格 |
| condition_level | VARCHAR(32) | 成色：`NEW`、`LIKE_NEW`、`GOOD`、`NORMAL` |
| trade_location | VARCHAR(128) | 校内交易地点 |
| cover_image | VARCHAR(255) | 封面图 URL |
| status | VARCHAR(32) | 商品状态，见 `GoodsStatus` |
| audit_reason | VARCHAR(500) | 最近一次驳回原因 |
| view_count | INT | 浏览量 |
| favorite_count | INT | 收藏数 |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

索引：

| 索引 | 字段 | 说明 |
| --- | --- | --- |
| idx_goods_status | status | 商品状态筛选 |
| idx_goods_category | category_id | 分类筛选 |
| idx_goods_seller | seller_id | 我的商品 |
| idx_goods_title | title | 标题模糊搜索辅助 |

说明：不再设置 `audit_status` 字段，审核状态由 `status` 表示。

## market_goods_image

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | BIGINT PK | 图片 ID |
| goods_id | BIGINT | 商品 ID |
| image_url | VARCHAR(255) | 图片 URL |
| sort | INT | 排序 |
| created_at | DATETIME | 创建时间 |

索引：`idx_image_goods(goods_id)`。

## market_order

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | BIGINT PK | 订单 ID |
| order_no | VARCHAR(64) | 订单编号，唯一 |
| buyer_id | BIGINT | 买家 ID |
| seller_id | BIGINT | 卖家 ID |
| goods_id | BIGINT | 商品 ID |
| amount | DECIMAL(10,2) | 订单金额，来自商品价格 |
| status | VARCHAR(32) | `CREATED`、`PAID`、`CANCELED`、`FINISHED` |
| created_at | DATETIME | 创建时间 |
| updated_at | DATETIME | 更新时间 |

索引：`uk_order_no(order_no)`、`idx_order_buyer(buyer_id)`、`idx_order_seller(seller_id)`、`idx_order_goods(goods_id)`、`idx_order_status(status)`。

## payment_record

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | BIGINT PK | 支付记录 ID |
| pay_no | VARCHAR(64) | 支付编号，唯一 |
| order_id | BIGINT | 订单 ID |
| amount | DECIMAL(10,2) | 支付金额 |
| pay_type | VARCHAR(32) | `MOCK` |
| pay_status | VARCHAR(32) | `WAITING`、`SUCCESS`、`FAILED`、`CLOSED` |
| paid_at | DATETIME | 支付成功时间 |
| created_at | DATETIME | 创建时间 |

索引：`uk_pay_no(pay_no)`、`idx_payment_order(order_id)`。

## order_log

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | BIGINT PK | 日志 ID |
| order_id | BIGINT | 订单 ID |
| operator_id | BIGINT | 操作人 ID |
| action | VARCHAR(64) | `CREATE`、`PAY`、`CANCEL`、`FINISH` |
| from_status | VARCHAR(32) | 原状态 |
| to_status | VARCHAR(32) | 新状态 |
| remark | VARCHAR(500) | 备注 |
| created_at | DATETIME | 创建时间 |

## goods_audit_log

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | BIGINT PK | 审核日志 ID |
| goods_id | BIGINT | 商品 ID |
| admin_id | BIGINT | 管理员 ID |
| result | VARCHAR(32) | `APPROVED`、`REJECTED` |
| reason | VARCHAR(500) | 审核说明 |
| created_at | DATETIME | 创建时间 |

## market_favorite

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | BIGINT PK | 收藏 ID |
| user_id | BIGINT | 用户 ID |
| goods_id | BIGINT | 商品 ID |
| created_at | DATETIME | 创建时间 |

索引：`uk_user_goods(user_id, goods_id)`、`idx_favorite_user(user_id)`。

## market_message

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | BIGINT PK | 消息 ID |
| sender_id | BIGINT | 发送人 |
| receiver_id | BIGINT | 接收人 |
| goods_id | BIGINT | 关联商品，可空 |
| order_id | BIGINT | 关联订单，可空 |
| content | VARCHAR(1000) | 内容 |
| read_status | VARCHAR(20) | `UNREAD`、`READ` |
| created_at | DATETIME | 创建时间 |

## market_review

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | BIGINT PK | 评价 ID |
| order_id | BIGINT | 订单 ID，唯一 |
| reviewer_id | BIGINT | 评价人 |
| target_user_id | BIGINT | 被评价用户 |
| goods_id | BIGINT | 商品 ID |
| rating | INT | 1-5 分 |
| content | VARCHAR(500) | 评价内容 |
| created_at | DATETIME | 创建时间 |

## market_report

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | BIGINT PK | 举报 ID |
| reporter_id | BIGINT | 举报人 |
| target_type | VARCHAR(32) | `GOODS`、`USER`、`MESSAGE`、`ORDER` |
| target_id | BIGINT | 目标 ID |
| reason | VARCHAR(128) | 举报原因 |
| description | VARCHAR(1000) | 详细说明 |
| status | VARCHAR(32) | `PENDING`、`PROCESSING`、`RESOLVED`、`REJECTED` |
| handler_id | BIGINT | 处理管理员 |
| handle_result | VARCHAR(1000) | 处理结果 |
| created_at | DATETIME | 创建时间 |
| handled_at | DATETIME | 处理时间 |

## admin_operation_log

| 字段 | 类型 | 说明 |
| --- | --- | --- |
| id | BIGINT PK | 日志 ID |
| admin_id | BIGINT | 管理员 ID |
| operation_type | VARCHAR(64) | 操作类型 |
| target_type | VARCHAR(64) | 操作对象类型 |
| target_id | BIGINT | 操作对象 ID |
| content | VARCHAR(1000) | 操作内容 |
| created_at | DATETIME | 创建时间 |
