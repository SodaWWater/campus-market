# 99 自我审核报告

## 审核结论

本轮已对 `docs/design` 设计文档进行一致性修复和商业化升级。当前文档可以作为后续代码生成的设计依据，但代码实现仍应分阶段进行，不建议一次性实现全部功能。

## 已修复的问题

| 编号 | 问题 | 修复结果 |
| --- | --- | --- |
| 1 | 架构文档中出现 RabbitMQ，与项目边界冲突 | 已删除 RabbitMQ、MQ、事件总线相关设计，日志直接在事务中写数据库 |
| 2 | 商品状态和审核状态重复 | 已取消独立 `audit_status`，统一使用 `goods.status` 表示审核和交易状态 |
| 3 | 图片上传被放到后期 | 已将图片上传提前到 V1 |
| 4 | 商品浏览强制登录不合理 | 已明确商品列表、详情、分类为游客可访问 |
| 5 | API 路径风格混乱 | 已统一为复数 REST 风格 `/api/goods`、`/api/orders` 等 |
| 6 | 缺少文件上传接口 | 已新增 `POST /api/files/upload` 设计 |
| 7 | 用户资料字段不足 | `sys_user` 已补充 school、major、grade、bio、credit_score |
| 8 | 缺少订单并发控制 | 已补充条件更新锁定商品的设计和测试用例 |
| 9 | V1 路线图范围不合理 | 已调整 V1 为最小交易闭环 + 商品审核 + 图片上传 |
| 10 | 部署文档包含 RabbitMQ | 已移除 RabbitMQ 端口和启动要求 |

## 当前单一事实源

| 事实类型 | 文件 |
| --- | --- |
| 项目边界 | `00-project-charter.md` |
| 功能优先级 | `01-prd.md`、`13-development-roadmap.md` |
| 状态机 | `04-state-machine.md` |
| 数据库 | `05-database-design.md` |
| API | `06-api-design.md` |
| 架构约束 | `07-backend-architecture.md` |
| 安全约束 | `09-security-design.md` |

后续代码、README、接口文档、简历和面试材料必须与上述文件一致。

## 当前最终状态机

### 商品状态

`DRAFT -> PENDING_AUDIT -> ON_SALE -> LOCKED -> SOLD`

其他分支：

- `PENDING_AUDIT -> REJECTED`
- `REJECTED -> PENDING_AUDIT`
- `ON_SALE -> OFF_SHELF`
- `LOCKED -> ON_SALE`，用于未支付取消或超时取消

### 订单状态

`CREATED -> PAID -> FINISHED`

取消分支：

- `CREATED -> CANCELED`

### 支付状态

`WAITING -> SUCCESS / FAILED / CLOSED`

### 举报状态

`PENDING -> PROCESSING -> RESOLVED / REJECTED`

## 当前最终 V1-V5 路线图

| 版本 | 目标 | 核心功能 |
| --- | --- | --- |
| V1 | 最小交易闭环 + 商品审核 | 登录、资料、图片、发布、审核、浏览、下单、支付、取消、完成、我的商品、我的订单、简单页面 |
| V2 | 后台运营增强 | 分类、看板、用户、商品、订单、操作日志 |
| V3 | 用户互动闭环 | 收藏、消息、评价、举报 |
| V4 | 前端体验升级 | Vue 3 用户端和后台端 |
| V5 | 工程化和面试材料 | Docker、CI、测试脚本、README、简历、面试文档 |

## 一致性检查结果

| 检查项 | 结果 |
| --- | --- |
| 是否还有 RabbitMQ | 未发现应保留的 RabbitMQ 设计 |
| 是否还使用 audit_status | 数据库和领域设计已改为不使用 audit_status |
| API 是否统一复数 REST 风格 | 已统一 |
| 商品浏览是否游客可访问 | 已明确 |
| 文件上传是否设计 | 已补充 |
| 订单并发控制是否设计 | 已补充 |
| V1 是否包含图片和审核 | 已包含 |
| 是否仍写微服务/高并发/分布式 | 未作为目标设计 |

## 仍需人工确认的问题

1. 当前设计建议 V1 使用本地图片存储，后续是否升级 MinIO 可等 V5 再决定。
2. V1 是否实现草稿 `DRAFT` 可以按时间决定；如果开发压力大，可先让发布直接进入 `PENDING_AUDIT`。
3. V3 的站内消息第一版使用普通 HTTP，是否升级 WebSocket 可后续决定。
4. Vue 3 前端建议放到 V4，不建议现在立刻做。

## 后续代码生成建议

下一阶段不要一次性生成所有代码。建议先生成 V1：

1. 扩展数据库和枚举。
2. 实现用户资料和文件上传。
3. 实现商品发布、图片、审核、列表、详情。
4. 实现订单创建、取消、模拟支付、完成。
5. 实现简单静态页面。
6. 编译通过后再进入 V2。
