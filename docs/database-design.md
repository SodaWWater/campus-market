# 数据库设计

数据库：`campus_market`。SQL 字段使用下划线，Java Entity 使用驼峰，MyBatis-Plus 开启 `map-underscore-to-camel-case`。

| 表名 | 说明 | 主要字段 |
| --- | --- | --- |
| `sys_user` | 用户 | `id`, `username`, `password`, `nickname`, `phone`, `role`, `created_at`, `updated_at` |
| `market_category` | 分类 | `id`, `name`, `sort`, `status`, `created_at`, `updated_at` |
| `market_goods` | 商品 | `id`, `seller_id`, `category_id`, `title`, `description`, `price`, `status`, `view_count`, `created_at`, `updated_at` |
| `market_order` | 订单 | `id`, `order_no`, `buyer_id`, `seller_id`, `goods_id`, `amount`, `status`, `created_at`, `updated_at` |
| `market_order_event_log` | 订单事件日志 | `id`, `order_id`, `goods_id`, `buyer_id`, `event_type`, `status`, `created_at` |

商品状态：`ON_SALE`、`LOCKED`、`SOLD`、`OFF_SHELF`。

订单状态：`CREATED`、`PAID`、`CANCELED`、`FINISHED`。

初始化脚本：

- `scripts/init.sql`
- `scripts/sample-data.sql`
