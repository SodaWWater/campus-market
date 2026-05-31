# Campus Market Platform

校园二手交易平台 — 个人学习、复现与二次开发项目。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端 | Java 17 · Spring Boot 3.3 · Spring Security 6 · JWT · MyBatis-Plus 3.5 · SpringDoc OpenAPI |
| 数据库 | MySQL 8.0 |
| 缓存 | Redis 7 — 分类列表缓存、热商品页缓存（带降级） |
| 前端 | Vue 3 · Vite · Element Plus · Pinia · Vue Router 4 · Axios |
| 工程化 | Docker Compose · GitHub Actions CI |

## 功能概览

### 用户端
- 注册登录 / JWT 鉴权 / BCrypt 密码加密
- 商品广场（关键词搜索、分类筛选、价格区间）
- 商品详情（图片、描述、卖家信息、收藏）
- 商品发布 → 管理员审核 → 上架
- 下单锁定（条件更新防并发）、模拟支付、取消、确认完成
- 我的商品（发布管理、下架）、我买到的 / 我卖出的订单
- 收藏夹、站内消息（会话 + 未读数）、交易评价
- 举报提交

### 后台管理（ADMIN）
- 数据看板（用户/商品/订单统计）
- 商品审核（通过/驳回）、商品管理（强制下架）
- 分类管理（新增/修改/启用/禁用）
- 用户管理（禁用/启用）
- 订单管理、举报处理
- 管理员操作日志

## 数据库

13 张表：`sys_user` · `market_category` · `market_goods` · `market_goods_image` · `market_order` · `payment_record` · `order_log` · `goods_audit_log` · `market_favorite` · `market_message` · `market_review` · `market_report` · `admin_operation_log`

## 快速启动

```bash
# 1. 启动 MySQL + Redis
docker compose up -d

# 2. 启动后端 (端口 8082)
mvn spring-boot:run

# 3. 启动前端 (端口 15173)
cd frontend
npm install
npm run dev
```

## 访问地址

| 地址 | 说明 |
|------|------|
| http://localhost:15173 | 前端（用户端 + 后台管理） |
| http://localhost:8082/swagger-ui.html | Swagger API 文档 |
| http://localhost:8082 | 后端 + V1 演示页 |
| http://localhost:6380 | Redis（Docker） |

## 测试账号

| 用户名 | 密码 | 角色 |
|--------|------|------|
| admin | 123456 | ADMIN |
| seller1 | 123456 | USER |
| buyer1 | 123456 | USER |

## 接口示例

```bash
# 健康检查 (公开)
curl http://localhost:8082/api/health

# 登录
curl -X POST http://localhost:8082/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"buyer1","password":"123456"}'

# 查询商品 (公开)
curl http://localhost:8082/api/goods/page

# 发布商品 (需登录)
curl -X POST http://localhost:8082/api/goods \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d '{"categoryId":1,"title":"Java书","description":"二手教材","price":30,"conditionLevel":"GOOD","tradeLocation":"图书馆","imageUrls":[]}'

# 管理员审核
curl -X PUT http://localhost:8082/api/admin/goods/1/approve \
  -H "Authorization: Bearer <admin-token>"
```

完整接口见 `docs/api.md` 和 Swagger UI。

## 项目结构

```
├── src/main/java/com/liminghan/market/
│   ├── common/          # Result, ErrorCode, GlobalExceptionHandler
│   ├── config/          # Security, MyBatis-Plus, Redis, WebMvc, OpenAPI
│   ├── controller/      # 12 Controllers
│   ├── dto/             # Request DTOs
│   ├── entity/          # 13 Entity classes
│   ├── mapper/          # MyBatis-Plus Mappers
│   ├── security/        # JWT Filter, SecurityUser, SecurityContextUtil
│   ├── service/         # Service interfaces + impl
│   └── vo/              # Response VOs
├── frontend/src/
│   ├── api/             # Axios HTTP client + API functions
│   ├── layouts/         # MainLayout (用户端) + AdminLayout (后台)
│   ├── router/          # Vue Router (19 routes + guards)
│   ├── stores/          # Pinia auth store
│   └── views/           # 20 view components (user + admin)
├── scripts/             # init.sql + sample-data.sql
└── docker-compose.yml   # MySQL + Redis
```

## 面试准备

- `docs/design/` — 完整设计文档（PRD、架构、状态机、API、数据库、安全、部署）
- `docs/design/14-interview-guide.md` — 面试讲解指南
- `docs/interview-qa.md` — 30 题面试问答（含代码定位）
- `docs/resume-description.md` — 简历项目描述

## 参考与致谢

参考常见校园二手交易和后台管理项目的公开思路，结合实习简历场景做二次开发。
