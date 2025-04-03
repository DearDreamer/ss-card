 # 会员卡系统接口文档

## 基本信息

- 基础URL: `http://localhost:8080`
- 请求内容类型: `application/json`
- 响应内容类型: `application/json`

## 目录

1. [用户认证接口](#用户认证接口)
   - [用户登录](#用户登录)
   - [用户注册](#用户注册)
2. [用户信息接口](#用户信息接口)
   - [获取用户信息](#获取用户信息)
3. [交易记录接口](#交易记录接口)
   - [获取消费记录](#获取消费记录)
   - [获取充值记录](#获取充值记录)

## 用户认证接口

### 用户登录

用户登录，验证手机号和密码。

- **URL**: `/api/user/login`
- **方法**: POST
- **是否需要认证**: 否
- **请求参数**:

```json
{
  "username": "13812345678",  // 用户手机号
  "password": "password123"   // 用户密码
}
```

- **成功响应** (状态码: 200):

```json
{
  "success": true,
  "message": "登录成功",
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",  // JWT令牌
  "data": {
    "username": "13812345678"
  }
}
```

- **失败响应** (状态码: 200，登录失败):

```json
{
  "success": false,
  "message": "用户名或密码错误"
}
```

- **失败响应** (状态码: 400，参数验证失败):

```json
{
  "success": false,
  "message": "输入参数验证失败",
  "errors": {
    "username": "用户名不能为空",
    "password": "密码不能为空"
  }
}
```

### 用户注册

用户注册，创建新账号。

- **URL**: `/api/user/register`
- **方法**: POST
- **是否需要认证**: 否
- **请求参数**:

```json
{
  "username": "13812345678",    // 用户手机号
  "password": "加密后的密码",    // AES加密后的用户密码
  "confirmPassword": "加密后的密码"  // AES加密后的确认密码
}
```

- **成功响应** (状态码: 200):

```json
{
  "success": true,
  "message": "注册成功"
}
```

- **失败响应** (状态码: 200，注册失败):

```json
{
  "success": false,
  "message": "注册失败，请检查输入信息"
}
```

- **常见失败原因**:
  - 用户名已存在
  - 两次密码不一致
  - 参数格式不正确

## 用户信息接口

### 获取用户信息

获取当前登录用户的基本信息。

- **URL**: `/api/user/info`
- **方法**: GET
- **是否需要认证**: 是
- **请求头**: 
  ```
  Authorization: Basic base64(username:password)
  ```
- **请求参数**: 无

- **成功响应** (状态码: 200):

```json
{
  "phone": "13812345678",
  "cardType": "黄金会员",
  "balance": "1000.00",
  "lastVisit": "2024-04-01 15:30"
}
```

- **失败响应** (状态码: 401，未认证):

```json
{
  "success": false,
  "message": "未经授权的访问"
}
```

- **失败响应** (状态码: 500，服务器错误):

```json
{
  "success": false,
  "message": "服务器内部错误"
}
```

## 交易记录接口

### 获取消费记录

获取当前登录用户的消费记录列表。

- **URL**: `/api/records/consumption`
- **方法**: GET
- **是否需要认证**: 是
- **请求头**: 
  ```
   Authorization: Bearer <token>
  ```
- **请求参数**: 无

- **成功响应** (状态码: 200):

```json
[
  { "time": "2024-04-01 10:00", "amount": "200.00" },
  { "time": "2024-03-28 14:30", "amount": "150.00" },
  { "time": "2024-03-25 09:20", "amount": "300.00" }
]
```

- **失败响应** (状态码: 401，未认证):

```json
{
  "success": false,
  "message": "未经授权的访问"
}
```

- **失败响应** (状态码: 500，服务器错误):

```json
{
  "success": false,
  "message": "服务器内部错误"
}
```

### 获取充值记录

获取当前登录用户的充值记录列表。

- **URL**: `/api/records/recharge`
- **方法**: GET
- **是否需要认证**: 是
- **请求头**: 
  ```
  Authorization: Bearer <token>
  ```
- **请求参数**: 无

- **成功响应** (状态码: 200):

```json
[
  { "time": "2024-04-02 11:00", "amount": "500.00" },
  { "time": "2024-03-29 16:45", "amount": "300.00" },
  { "time": "2024-03-26 12:10", "amount": "400.00" }
]
```

- **失败响应** (状态码: 401，未认证):

```json
{
  "success": false,
  "message": "未经授权的访问"
}
```

- **失败响应** (状态码: 500，服务器错误):

```json
{
  "success": false,
  "message": "服务器内部错误"
}
```

## 认证说明

本API使用Authentication结合jwt认证。请求需要在头部包含`Authorization`字段，值为用户名和密码生成的jwt。

示例:
- 用户名: `13812345678`  
- 密码: `password123`
- 拼接: 根据`13812345678:password123`生成的jwt
- jwt: `MTM4MTIzNDU2Nzg6cGFzc3dvcmQxMjM=`
- 最终请求头: `Authorization:MTM4MTIzNDU2Nzg6cGFzc3dvcmQxMjM=`


## 错误码说明

- 200: 成功
- 400: 请求参数错误
- 401: 未经授权的访问
- 403: 访问被拒绝
- 404: 资源不存在  
- 500: 服务器内部错误

## 开发环境测试账户

系统初始化后预置的测试账户:

- 用户名/手机号: `13800138000`
- 密码: `123456`
- 会员级别: `黄金会员`
- 余额: `1000.00`