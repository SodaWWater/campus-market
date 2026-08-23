# 10 缓存设计

## 缓存原则

Redis 只用于提升查询体验，不作为核心交易状态唯一数据源。订单、支付、商品锁定状态等强一致数据以 MySQL 为准。

## 缓存 Key 设计

| Key | 类型 | 内容 | 过期时间 | 阶段 | 更新策略 |
| --- | --- | --- | --- | --- | --- |
| `market:category:list` | String/JSON | 启用分类列表 | 30 分钟 | V1 | 分类新增、修改、禁用后删除 |
| `market:goods:hot` | String/JSON | 热门商品列表 | 10 分钟 | V4 | 商品审核、下架、支付、收藏变化后删除 |
| `market:goods:view:{goodsId}` | Counter | 浏览量增量 | 1 天 | V4 | 定时或查询时回写 MySQL |
| `market:user:unread:{userId}` | Counter | 用户未读消息数 | 30 分钟 | V3 | 发送消息增加，已读后重算/删除 |

## 分类缓存

使用场景：

1. 商品发布页选择分类。
2. 商品广场筛选分类。
3. 后台分类列表。

读取流程：

1. 先读 `market:category:list`。
2. 命中则返回。
3. 未命中查询 MySQL 中 `status = ENABLE` 的分类。
4. 写入 Redis。

失效时机：

1. 新增分类。
2. 修改分类名称或排序。
3. 启用或禁用分类。

Redis 不可用时降级查 MySQL，不影响主流程。

## 热门商品缓存

热门商品排序建议：

1. `view_count`。
2. `favorite_count`。
3. `created_at` 倒序。

只缓存前 10 到 20 条，适合首页展示。

## 浏览量缓存

V1 可以直接更新 MySQL，V4 再改为 Redis 增量：

```text
market:goods:view:{goodsId}
```

详情展示时可以显示：

```text
MySQL view_count + Redis 增量
```

## 未读消息数缓存

V3 站内消息模块使用：

```text
market:user:unread:{userId}
```

用户读取消息后删除或重算缓存。

## 明确不缓存

| 数据 | 原因 |
| --- | --- |
| 订单详情 | 涉及强一致状态 |
| 支付状态 | 必须以数据库为准 |
| 商品锁定状态 | 影响交易一致性 |
| 用户权限 | 以 JWT 和数据库状态为准 |

## 实现说明

> Redis 主要缓存读多写少、允许短时间不完全一致的数据，例如分类列表和热门商品。订单、支付、商品锁定这类强一致数据不依赖缓存，避免影响交易正确性。
