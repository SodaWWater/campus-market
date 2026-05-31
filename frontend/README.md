# campus-market-admin frontend

Vue 3 + Vite + Element Plus + Pinia 前端，用于演示校园二手交易与后台管理系统。

## 功能

- 登录 / 注册
- Axios 自动携带 JWT
- 商品发布和分页查询
- 分类列表、ADMIN 分类创建
- 订单创建、支付、取消、完成
- ADMIN 用户、商品、订单入口

## 启动

先启动后端 `campus-market-admin`，再执行：

```powershell
npm install
npm run dev
```

默认访问：

- 前端：http://localhost:5174
- 后端代理：http://localhost:8082

Vite 会把 `/api` 代理到后端。
