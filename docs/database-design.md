# 数据库设计

数据库名：`campus_market`

字段命名约定：

- SQL 字段使用下划线命名。
- Java 实体字段使用驼峰命名。
- MyBatis-Plus 使用 `map-underscore-to-camel-case: true` 映射。

## 表结构

- `sys_user`：用户表，角色只保留 `USER`、`ADMIN`
- `market_category`：商品分类表
- `market_goods`：商品表，状态为 `ON_SALE`、`LOCKED`、`SOLD`、`OFF_SHELF`
- `market_order`：订单表，状态为 `CREATED`、`PAID`、`CANCELED`、`FINISHED`

完整建表语句见 `scripts/init.sql`，演示数据见 `scripts/sample-data.sql`。
