-- 创建用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    phone_number VARCHAR(20),
    card_type VARCHAR(20) DEFAULT '普通会员',
    balance DECIMAL(10, 2) DEFAULT 0.00,
    total_recharge DECIMAL(10, 2) DEFAULT 0.00,
    total_spent DECIMAL(10, 2) DEFAULT 0.00,
    last_visit DATETIME,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL
);

-- 创建交易记录表
CREATE TABLE IF NOT EXISTS transactions (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    phone_number VARCHAR(20) not null,
    amount DECIMAL(10, 2) NOT NULL,
    transaction_time DATETIME NOT NULL,
    transaction_type VARCHAR(20) NOT NULL,
    transaction_desc VARCHAR(100),
    created_at DATETIME NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id)
    );


-- 添加测试数据（可选）
INSERT INTO users (username, password, phone_number, card_type, balance, created_at, updated_at)
VALUES
    ('13800138000', '$2a$10$rJ8CZRIKkGHR1JpZdRCnJe./d.Yjz53OL5AuL/lVc7QrHs1z84GU.', '13800138000', '黄金会员', 1000.00, NOW(), NOW());

-- 密码是: 123456 