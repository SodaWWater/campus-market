# Redis 使用说明

当前阶段 Redis 用于缓存商品分类列表。

## 分类缓存

- key：`market:category:list`
- 查询分类列表时先读 Redis
- Redis 无数据时查询 MySQL
- 查询结果写入 Redis，过期时间 1 小时
- 新增、修改、删除分类后删除缓存

## 降级策略

Redis 不可用时，分类查询会直接走 MySQL，不影响主流程。
