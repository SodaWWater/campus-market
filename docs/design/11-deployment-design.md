# 11 部署设计

## 本地启动目标

本地开发环境需要能启动：

1. Spring Boot 后端。
2. MySQL。
3. Redis，可选但建议安装。
4. 前端页面，V1 可直接由 Spring Boot `static/index.html` 提供。
5. Swagger 文档。

不引入 RabbitMQ。

## 端口规划

| 服务 | 地址 |
| --- | --- |
| 后端 API | `http://localhost:8082` |
| Swagger | `http://localhost:8082/swagger-ui.html` |
| V1 静态前端 | `http://localhost:8082/` |
| MySQL | `localhost:3307` 或本机默认 `3306` |
| Redis | `localhost:6380` 或本机默认 `6379` |

实际端口以 `application.yml` 为准。设计文档建议商业化升级后使用 `8082`，避免和 AI 项目 `8081` 冲突。

## 本地启动步骤

```powershell
cd campus-market-platform
mysql -uroot -proot < scripts/init.sql
mysql -uroot -proot < scripts/sample-data.sql
mvn -q -DskipTests compile
mvn spring-boot:run
```

浏览器访问：

```text
http://localhost:8082/
http://localhost:8082/swagger-ui.html
```

## Docker Compose 可选启动

Docker Compose 只用于基础设施：

```powershell
docker compose up -d
```

建议包含：

1. MySQL。
2. Redis。

不强制把后端和前端放入 Docker，便于 IDEA 调试和本地演示。

## MySQL 初始化

初始化脚本：

1. `scripts/init.sql`：建库建表。
2. `scripts/sample-data.sql`：测试数据。

检查：

```sql
USE campus_market;
SHOW TABLES;
SELECT * FROM sys_user;
SELECT * FROM market_goods;
```

## Redis

Redis 用途：

1. 分类缓存。
2. 热门商品缓存。
3. 浏览量增量。
4. 未读消息数。

Redis 不可用时，后端应降级查询 MySQL。

## 文件上传目录

V1 本地目录：

```text
uploads/goods/
uploads/avatar/
```

建议配置项：

```yaml
file:
  upload-dir: uploads
  access-prefix: /uploads
```

Spring Boot 需要配置静态资源映射，使 `/uploads/**` 可以访问本地文件。

## 默认测试账号

| 账号 | 密码 | 角色 |
| --- | --- | --- |
| admin | 123456 | ADMIN |
| student1 | 123456 | USER |
| student2 | 123456 | USER |

密码入库必须是 BCrypt hash，不能是明文。
