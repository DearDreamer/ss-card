# 会员卡系统API文档

## 基本信息

- 基础URL: `http://localhost:8080`
- 内容类型: `application/json`
- 字符编码: UTF-8
- 认证方式: Bearer Token (JWT)

## 通用响应格式

### 1. 成功响应 (HTTP 200)

```json
{
  "success": true,        // 布尔值，表示请求是否成功
  "message": "xxx",       // 字符串，响应消息
  "statusCode": 200,      // 数字，状态码
  "data": {},            // 可选，响应数据
  "token": "xxx"         // 可选，仅登录接口返回JWT令牌
}
```

### 2. 业务失败响应 (HTTP 200)

```json
{
  "success": false,       // 布尔值，表示请求失败
  "message": "xxx",       // 字符串，错误信息
  "statusCode": 200      // 数字，状态码
}
```

### 3. 认证失败响应 (HTTP 401)

```json
{
  "success": false,       // 布尔值，表示请求失败
  "message": "未经授权的访问", // 字符串，错误信息
  "statusCode": 401      // 数字，状态码
}
```

当认证失败时，服务器将返回401状态码。前端将自动：
1. 清除本地存储的认证信息
2. 显示"登录已过期，请重新登录"提示
3. 跳转到登录页面

### 4. 服务器错误响应 (HTTP 500)

```json
{
  "success": false,       // 布尔值，表示请求失败
  "message": "服务器内部错误", // 字符串，错误信息
  "statusCode": 500      // 数字，状态码
}
```

## API接口详情

### 1. 用户认证相关接口

#### 1.1 用户登录

- **URL**: `/api/user/login`
- **方法**: POST
- **是否需要认证**: 否
- **请求参数**:

```json
{
  "username": "string",  // 手机号（11位）
  "password": "string"   // AES加密后的密码
}
```

- **成功响应**:

```json
{
  "success": true,
  "message": "登录成功",
  "statusCode": 200,
  "token": "eyJhbGciOiJIUzI1NiIs...",  // JWT令牌
  "data": {
    "userId": "string",
    "username": "string"
  }
}
```

- **失败响应**:

```json
{
  "success": false,
  "message": "用户名或密码错误",
  "statusCode": 200
}
```

#### 1.2 用户注册

- **URL**: `/api/user/register`
- **方法**: POST
- **是否需要认证**: 否
- **请求参数**:

```json
{
  "username": "string",       // 手机号（11位）
  "password": "string",      // AES加密后的密码
  "confirmPassword": "string" // AES加密后的确认密码
}
```

- **成功响应**:

```json
{
  "success": true,
  "message": "注册成功",
  "statusCode": 200
}
```

- **失败响应**:

```json
{
  "success": false,
  "message": "注册失败原因",
  "statusCode": 200
}
```

### 2. 用户信息相关接口

#### 2.1 获取用户信息

- **URL**: `/api/user/info`
- **方法**: GET
- **是否需要认证**: 是
- **请求头**:
```
Authorization: Bearer {token}
```

- **成功响应**:

```json
{
  "success": true,
  "message": "获取成功",
  "statusCode": 200,
  "data": {
    "username": "string",    // 手机号
    "balance": "1000.00",    // 账户余额
    "memberLevel": "string", // 会员等级
    "points": number        // 积分
  }
}
```

- **失败响应**:

```json
{
  "success": false,
  "message": "获取用户信息失败",
  "statusCode": 200
}
```

## 安全说明

### 1. 密码加密

前端使用AES-ECB模式对密码进行加密，具体配置如下：
- 加密模式：ECB
- 填充方式：PKCS7Padding
- 密钥：16位固定密钥（需要与后端协商）

### 2. 认证流程

1. 用户登录/注册时，密码使用AES加密传输
2. 登录成功后，服务器返回JWT令牌
3. 后续请求使用Bearer Token认证方式
4. Token过期或无效时，需要重新登录

### 3. 错误处理

- HTTP 200: 正常响应，需要查看响应体中的success字段
- HTTP 401: 未认证或认证失败
- HTTP 500: 服务器内部错误

## 状态码说明

- 200: 请求成功
- 400: 请求参数错误
- 401: 未认证或认证失败
- 403: 权限不足
- 404: 资源不存在
- 500: 服务器内部错误

## 测试账号

为方便开发测试，系统预置以下测试账号：

```
手机号: 13800138000
密码: 123456
会员等级: 普通会员
初始余额: 1000.00
```

## 错误处理说明

### 1. HTTP状态码处理

- **200 OK**：
  - 检查响应体中的 `success` 和 `statusCode` 字段
  - `success: true` 表示业务成功
  - `success: false` 表示业务失败，错误信息在 `message` 字段中

- **401 Unauthorized**：
  - 表示用户未认证或认证已过期
  - 响应中 `statusCode: 401`
  - 前端会自动清除认证信息
  - 自动跳转到登录页面
  - 显示"登录已过期，请重新登录"提示

- **500 Internal Server Error**：
  - 表示服务器内部错误
  - 响应中 `statusCode: 500`
  - 前端显示"网络请求失败"提示

### 2. 错误响应示例

1. 认证失败 (401):
```http
HTTP/1.1 401 Unauthorized
Content-Type: application/json

{
  "success": false,
  "message": "未经授权的访问",
  "statusCode": 401
}
```

2. 业务错误 (200):
```http
HTTP/1.1 200 OK
Content-Type: application/json

{
  "success": false,
  "message": "用户名或密码错误",
  "statusCode": 200
}
```

3. 服务器错误 (500):
```http
HTTP/1.1 500 Internal Server Error
Content-Type: application/json

{
  "success": false,
  "message": "服务器内部错误",
  "statusCode": 500
}
```
