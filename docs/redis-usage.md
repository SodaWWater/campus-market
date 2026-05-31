# Redis 使用说明

## 分类列表缓存

- key：`market:category:list`
- 写入位置：`CategoryServiceImpl#listCategories`
- 删除位置：新增、修改、删除分类后删除缓存
- 降级：Redis 异常时直接查 MySQL，不影响接口主流程

## 热商品页缓存

- key：`market:goods:hot`
- 写入位置：`GoodsServiceImpl#pageGoods`
- 缓存条件：第一页、无关键词、无分类筛选、size 不超过 10
- 删除位置：发布、修改、删除、管理员下架商品后删除缓存

Redis 在本项目中只作为性能优化，不作为核心数据来源。
