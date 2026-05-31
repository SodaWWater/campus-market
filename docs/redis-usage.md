# Redis 使用场景

## 缓存键

| 键 | 说明 | 数据 | TTL |
|----|------|------|-----|
| `market:category:list` | 分类列表 | 所有 ENABLE 分类 | 60 min（可配置） |
| `market:goods:hot` | 热商品页 | 第一页未筛选商品 | 10 min（可配置） |

## 缓存策略：Cache-Aside

1. 读：先查 Redis → 命中返回 → 未命中查 MySQL → 写回 Redis
2. 写（新增/修改/删除）：先更新 MySQL → 删除缓存 → 下次读回源

## 降级设计

所有 Redis 操作外层 try-catch。Redis 异常时：
- 分类查询：直接查 MySQL（`CategoryServiceImpl#listCategories`）
- 商品查询：直接查 MySQL（`GoodsServiceImpl#pageGoods`）
- 缓存驱逐：跳过（`AdminServiceImpl#offShelfGoods`）

业务不依赖 Redis 可用性，缓存仅作性能优化。

## 缓存驱逐位置

| 操作 | 文件 | 触发方法 |
|------|------|----------|
| 新增分类 | `CategoryServiceImpl` | `createCategory` → `evictCache()` |
| 修改分类 | `CategoryServiceImpl` | `updateCategory` → `evictCache()` |
| 启用分类 | `CategoryServiceImpl` | `enableCategory` → `evictCache()` |
| 禁用分类 | `CategoryServiceImpl` | `disableCategory` → `evictCache()` |
| 删除分类 | `CategoryServiceImpl` | `deleteCategory` → `evictCache()` |
| 发布商品 | `GoodsServiceImpl` | `createGoods` → `redisTemplate.delete` |
| 修改商品 | `GoodsServiceImpl` | `updateGoods` → `redisTemplate.delete` |
| 下架商品 | `GoodsServiceImpl` | `offShelfMyGoods` → `redisTemplate.delete` |
| 管理员下架 | `AdminServiceImpl` | `offShelfGoods` → `redisTemplate.delete` |
| 审核通过 | `GoodsServiceImpl` | `approveGoods` → `redisTemplate.delete` |

## 配置

```yaml
app:
  cache:
    category-list-ttl-minutes: 60
    hot-goods-ttl-minutes: 10
```
