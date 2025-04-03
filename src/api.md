# API Documentation

这是一个基于Java spring boot的后端项目，我的数据库的连接方式是：mysql://root:frrwm62m@ss-db-mysql.ns-4owchi21.svc:3306。

## 获取用户信息接口
- **URL**: `/api/user/info`
- **方法**: GET
- **描述**: 获取用户的基本信息，包括手机号、会员卡类型、卡内余额和上次到店时间。
- **请求参数**: 无
- **响应示例**:
  ```json
  {
    "phone": "13800138000",
    "cardType": "黄金会员",
    "balance": "1000.00",
    "lastVisit": "2024-04-01 15:30"
  }
  ```

## 获取消费记录接口
- **URL**: `/api/records/consumption`
- **方法**: GET
- **描述**: 获取用户的消费记录列表。
- **请求参数**: 无
- **响应示例**:
  ```json
  [
    { "time": "2024-04-01 10:00", "amount": "200.00" },
    { "time": "2024-03-28 14:30", "amount": "150.00" },
    { "time": "2024-03-25 09:20", "amount": "300.00" }
  ]
  ```

## 获取充值记录接口
- **URL**: `/api/records/recharge`
- **方法**: GET
- **描述**: 获取用户的充值记录列表。
- **请求参数**: 无
- **响应示例**:
  ```json
  [
    { "time": "2024-04-02 11:00", "amount": "500.00" },
    { "time": "2024-03-29 16:45", "amount": "300.00" },
    { "time": "2024-03-26 12:10", "amount": "400.00" }
  ]
  ```

## 用户登录接口
- **URL**: `/api/user/login`
- **方法**: POST
- **描述**: 用户登录，验证手机号和密码。
- **请求参数**:
  ```json
  {
    "username": "手机号",
    "password": "密码"
  }
  ```
- **响应示例**:
  ```json
  {
    "success": true,
    "message": "登录成功"
  }
  ```

## 用户注册接口
- **URL**: `/api/user/register`
- **方法**: POST
- **描述**: 用户注册，创建新账号。
- **请求参数**:
  ```json
  {
    "username": "手机号",
    "password": "密码",
    "confirmPassword": "确认密码"
  }
  ```
- **响应示例**:
  ```json
  {
    "success": true,
    "message": "注册成功"
  }
  ``` 
项目结构如下
src/
├── main/
│   ├── java/
│   │   └── com/
│   │       └── example/
│   │           └── membership/
│   │               ├── config/            # 配置类
│   │               │   ├── SecurityConfig.java
│   │               │   └── SwaggerConfig.java
│   │               ├── controller/        # API入口
│   │               │   ├── AuthController.java
│   │               │   ├── UserController.java
│   │               │   └── RecordController.java
│   │               ├── service/           # 业务逻辑
│   │               │   ├── impl/
│   │               │   │   ├── UserServiceImpl.java
│   │               │   │   └── AuthServiceImpl.java
│   │               │   ├── UserService.java
│   │               │   └── AuthService.java
│   │               ├── repository/        # 数据访问
│   │               │   ├── UserRepository.java
│   │               │   └── TransactionRepository.java
│   │               ├── model/             # 数据模型
│   │               │   ├── entity/
│   │               │   │   ├── User.java
│   │               │   │   └── Transaction.java
│   │               │   └── dto/          # 数据传输对象
│   │               │       ├── LoginRequest.java
│   │               │       ├── UserInfoDTO.java
│   │               │       └── TransactionDTO.java
│   │               ├── exception/         # 异常处理
│   │               │   ├── GlobalExceptionHandler.java
│   │               │   └── CustomException.java
│   │               └── MembershipApplication.java # 启动类
│   └── resources/
│       ├── application.yml               # 主配置文件
│       └── db/
│           └── migration/                # 数据库脚本
│               └── chema.sql