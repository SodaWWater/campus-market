# API 文档

统一返回格式：

```json
{"code":0,"message":"success","data":{}}
```

除健康检查、注册、登录外，其他接口都需要：

```text
Authorization: Bearer <token>
```

## 健康检查

- 接口名称：健康检查
- 请求路径：`/api/health`
- 请求方法：`GET`
- 请求参数 JSON：无
- Authorization Header：不需要

返回示例：

```json
{"code":0,"message":"success","data":"ok"}
```

## 注册

- 接口名称：注册
- 请求路径：`/api/auth/register`
- 请求方法：`POST`
- Authorization Header：不需要

请求参数 JSON：

```json
{"username":"student3","password":"123456","nickname":"Student Three","phone":"13800000003","role":"USER"}
```

返回示例：

```json
{"code":0,"message":"success","data":{"token":"jwt","userId":3,"username":"student3","role":"USER"}}
```

## 登录

- 接口名称：登录
- 请求路径：`/api/auth/login`
- 请求方法：`POST`
- Authorization Header：不需要

请求参数 JSON：

```json
{"username":"student1","password":"123456"}
```

返回示例：

```json
{"code":0,"message":"success","data":{"token":"jwt","userId":2,"username":"student1","role":"USER"}}
```

## 发布商品

- 接口名称：发布商品
- 请求路径：`/api/goods`
- 请求方法：`POST`
- Authorization Header：需要 USER 或 ADMIN token

请求参数 JSON：

```json
{"categoryId":1,"title":"Java Textbook","description":"Used book","price":35.00}
```

返回示例：

```json
{"code":0,"message":"success","data":{"id":1,"sellerId":2,"status":"ON_SALE"}}
```

## 修改商品

- 接口名称：修改商品
- 请求路径：`/api/goods/{id}`
- 请求方法：`PUT`
- Authorization Header：需要商品卖家 token

请求参数 JSON：

```json
{"categoryId":1,"title":"Java Textbook Updated","description":"Good condition","price":30.00,"status":"ON_SALE"}
```

返回示例：

```json
{"code":0,"message":"success","data":{"id":1,"title":"Java Textbook Updated"}}
```

## 删除商品

- 接口名称：删除商品
- 请求路径：`/api/goods/{id}`
- 请求方法：`DELETE`
- Authorization Header：需要商品卖家 token
- 请求参数 JSON：无

返回示例：

```json
{"code":0,"message":"success","data":"ok"}
```

## 商品分页

- 接口名称：商品分页
- 请求路径：`/api/goods/page?current=1&size=10&keyword=Java&categoryId=1`
- 请求方法：`GET`
- Authorization Header：需要 token
- 请求参数 JSON：无，使用 query 参数

返回示例：

```json
{"code":0,"message":"success","data":{"records":[],"total":0,"size":10,"current":1}}
```

## 商品详情

- 接口名称：商品详情
- 请求路径：`/api/goods/{id}`
- 请求方法：`GET`
- Authorization Header：需要 token

返回示例：

```json
{"code":0,"message":"success","data":{"id":1,"title":"Java Textbook","status":"ON_SALE"}}
```

## 分类列表

- 接口名称：分类列表
- 请求路径：`/api/category/list`
- 请求方法：`GET`
- Authorization Header：需要 token
- 请求参数 JSON：无

返回示例：

```json
{"code":0,"message":"success","data":[{"id":1,"name":"Books","status":"ENABLE"}]}
```

## 管理员新增分类

- 接口名称：管理员新增分类
- 请求路径：`/api/admin/category`
- 请求方法：`POST`
- Authorization Header：需要 ADMIN token

请求参数 JSON：

```json
{"name":"Books","sort":1,"status":"ENABLE"}
```

返回示例：

```json
{"code":0,"message":"success","data":{"id":1,"name":"Books"}}
```

## 管理员修改分类

- 接口名称：管理员修改分类
- 请求路径：`/api/admin/category/{id}`
- 请求方法：`PUT`
- Authorization Header：需要 ADMIN token

请求参数 JSON：

```json
{"name":"Books Updated","sort":1,"status":"ENABLE"}
```

返回示例：

```json
{"code":0,"message":"success","data":{"id":1,"name":"Books Updated"}}
```

## 管理员删除分类

- 接口名称：管理员删除分类
- 请求路径：`/api/admin/category/{id}`
- 请求方法：`DELETE`
- Authorization Header：需要 ADMIN token
- 请求参数 JSON：无

返回示例：

```json
{"code":0,"message":"success","data":"ok"}
```

## 创建订单

- 接口名称：创建订单
- 请求路径：`/api/order`
- 请求方法：`POST`
- Authorization Header：需要买家 token

请求参数 JSON：

```json
{"goodsId":1}
```

返回示例：

```json
{"code":0,"message":"success","data":{"id":1,"status":"CREATED","goodsId":1}}
```

## 我的订单

- 接口名称：我的订单
- 请求路径：`/api/order/my`
- 请求方法：`GET`
- Authorization Header：需要 token

返回示例：

```json
{"code":0,"message":"success","data":[]}
```

## 订单详情

- 接口名称：订单详情
- 请求路径：`/api/order/{id}`
- 请求方法：`GET`
- Authorization Header：需要订单买家、卖家或 ADMIN token

返回示例：

```json
{"code":0,"message":"success","data":{"id":1,"status":"CREATED"}}
```

## 支付订单

- 接口名称：支付订单
- 请求路径：`/api/order/{id}/pay`
- 请求方法：`PUT`
- Authorization Header：需要买家 token
- 请求参数 JSON：无

返回示例：

```json
{"code":0,"message":"success","data":{"id":1,"status":"PAID"}}
```

## 取消订单

- 接口名称：取消订单
- 请求路径：`/api/order/{id}/cancel`
- 请求方法：`PUT`
- Authorization Header：需要买家 token
- 请求参数 JSON：无

返回示例：

```json
{"code":0,"message":"success","data":{"id":1,"status":"CANCELED"}}
```

## 完成订单

- 接口名称：完成订单
- 请求路径：`/api/order/{id}/finish`
- 请求方法：`PUT`
- Authorization Header：需要订单买家或卖家 token
- 请求参数 JSON：无

返回示例：

```json
{"code":0,"message":"success","data":{"id":1,"status":"FINISHED"}}
```

## 管理员接口

| 接口名称 | 路径 | 方法 | Authorization Header | 返回示例 |
| --- | --- | --- | --- | --- |
| 用户列表 | `/api/admin/users` | GET | ADMIN token | `{"code":0,"message":"success","data":[]}` |
| 商品列表 | `/api/admin/goods` | GET | ADMIN token | `{"code":0,"message":"success","data":[]}` |
| 下架商品 | `/api/admin/goods/{id}/off` | PUT | ADMIN token | `{"code":0,"message":"success","data":{"status":"OFF_SHELF"}}` |
| 订单列表 | `/api/admin/orders` | GET | ADMIN token | `{"code":0,"message":"success","data":[]}` |

## 推荐测试顺序

1. `GET /api/health`
2. `POST /api/auth/register`
3. `POST /api/auth/login`
4. 携带 JWT 请求 `POST /api/goods` 发布商品
5. `GET /api/goods/page` 查询商品
6. `GET /api/category/list` 查询分类缓存
7. `POST /api/order` 创建订单
8. 使用 ADMIN token 访问 `/api/admin/goods`
