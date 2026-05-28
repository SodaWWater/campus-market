# 登录认证流程

当前阶段使用 Spring Security 6，不使用 `WebSecurityConfigurerAdapter`。

```mermaid
flowchart LR
    A["用户注册"] --> B["BCrypt 加密密码"]
    C["用户登录"] --> D["校验用户名和密码"]
    D --> E["JwtUtil 生成 token"]
    F["请求业务接口"] --> G["Authorization: Bearer token"]
    G --> H["JwtAuthenticationFilter"]
    H --> I["解析 SecurityUser"]
    I --> J["SecurityContext"]
    J --> K["访问受保护接口"]
```

## 权限规则

- `/api/health`、`/api/auth/register`、`/api/auth/login` 放行。
- `/api/admin/**` 仅允许 `ADMIN` 角色访问。
- 其他接口需要登录。

## 关键类

- `SecurityConfig`
- `JwtUtil`
- `JwtAuthenticationFilter`
- `CustomUserDetailsService`
- `SecurityUser`
