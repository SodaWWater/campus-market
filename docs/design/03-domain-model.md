# 03 领域模型

## 领域边界

系统采用单体模块化设计，不拆微服务。领域边界用于组织包、Service 和数据库表，核心围绕交易闭环和后台运营闭环。

| 领域 | 负责内容 |
| --- | --- |
| auth | 登录注册、JWT、密码加密、当前用户上下文 |
| user | 用户资料、账号状态、个人主页 |
| category | 分类维护、分类缓存 |
| goods | 商品发布、图片、审核状态、上下架、浏览 |
| favorite | 收藏与取消收藏 |
| order | 下单、取消、完成、订单日志 |
| payment | 模拟支付、支付记录 |
| message | 站内消息、未读数 |
| review | 交易评价 |
| report | 举报提交和举报处理 |
| admin | 后台看板、用户管理、商品管理、订单管理、操作日志 |
| file | 本地图片上传与访问 |

## 核心领域对象

### User

系统用户，可作为买家和卖家。管理员通过 `role=ADMIN` 访问后台。

| 属性 | 说明 |
| --- | --- |
| id | 用户 ID |
| username | 登录账号，唯一 |
| password | BCrypt 密码摘要 |
| nickname | 昵称 |
| phone | 手机号 |
| avatarUrl | 头像 URL |
| school | 学校 |
| major | 专业 |
| grade | 年级 |
| bio | 个人简介 |
| creditScore | 信用分，第一版只展示，默认 100 |
| role | `USER` 或 `ADMIN` |
| status | `ENABLE` 或 `DISABLED` |

边界：不做真实校园认证，不做复杂 RBAC。

### Category

商品分类，由管理员维护。

关系：一个分类包含多个商品。禁用分类后不能继续发布到该分类，历史商品保留分类 ID。

### Goods

二手商品，是交易闭环核心对象。

| 属性 | 说明 |
| --- | --- |
| sellerId | 卖家用户 ID |
| categoryId | 分类 ID |
| title | 标题 |
| description | 描述 |
| price | 售价 |
| conditionLevel | 成色：`NEW`、`LIKE_NEW`、`GOOD`、`NORMAL` |
| tradeLocation | 校内交易地点 |
| coverImage | 封面图 URL |
| status | 商品状态：`DRAFT`、`PENDING_AUDIT`、`ON_SALE`、`LOCKED`、`SOLD`、`OFF_SHELF`、`REJECTED` |
| auditReason | 驳回原因，只有被驳回时有值 |
| viewCount | 浏览量 |
| favoriteCount | 收藏数 |

重要规则：

1. 不单独维护 `audit_status`，审核阶段由 `status` 表示。
2. 审核记录通过 `goods_audit_log` 保存。
3. 二手商品默认一物一件，不设计库存。

### GoodsImage

商品图片。一个商品可有多张图片，第一张可作为封面。

### Favorite

用户收藏商品的关系。用户与商品是多对多关系。

规则：不能收藏自己的商品；同一用户对同一商品只能收藏一次。

### Order

订单对象，连接买家、卖家和商品。

| 属性 | 说明 |
| --- | --- |
| buyerId | 买家 ID |
| sellerId | 卖家 ID |
| goodsId | 商品 ID |
| orderNo | 订单编号 |
| amount | 订单金额，来自商品价格 |
| status | `CREATED`、`PAID`、`CANCELED`、`FINISHED` |

规则：订单金额以后端商品价格为准，不信任前端传入金额。

### PaymentRecord

模拟支付记录。项目不接真实第三方支付。

状态：`WAITING`、`SUCCESS`、`FAILED`、`CLOSED`。

### OrderLog

订单操作日志，记录创建、支付、取消、完成等动作。

### Message

站内消息。V3 使用 HTTP 接口实现，不做 WebSocket。

### Review

交易评价。只有完成订单才能评价，第一版允许买家评价卖家和商品。

### Report

举报对象。举报目标支持 `GOODS`、`USER`、`MESSAGE`、`ORDER`。

状态：`PENDING`、`PROCESSING`、`RESOLVED`、`REJECTED`。

### GoodsAuditLog

商品审核日志，记录管理员审核通过或驳回的原因。

### AdminOperationLog

管理员操作日志，记录审核、下架、禁用用户、处理举报等动作。
