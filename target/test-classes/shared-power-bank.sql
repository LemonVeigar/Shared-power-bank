
-- 初始化数据库
-- 如果数据库 `shared_power_bank` 不存在，则创建它
CREATE DATABASE IF NOT EXISTS `shared_power_bank` CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

-- 切换到刚刚创建的数据库
USE `shared_power_bank`;

-- 创建用户表
-- 该表用于存储用户的基本信息
CREATE TABLE IF NOT EXISTS users (
    id INT AUTO_INCREMENT PRIMARY KEY,                     -- 用户的唯一标识，主键，自动递增
    username VARCHAR(50) UNIQUE NOT NULL,                  -- 用户名，最大长度50，必须唯一，不允许为空
    password VARCHAR(128) NOT NULL,                        -- 用户密码（哈希值），最大长度128，不允许为空
    phone VARCHAR(20),                                     -- 用户手机号，可为空
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,         -- 用户注册时间，默认为当前时间
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- 用户信息最后更新时间，每次更新会自动修改
);

-- 创建充电宝表
-- 该表用于存储充电宝的基本信息
CREATE TABLE IF NOT EXISTS powerbanks (
    id INT AUTO_INCREMENT PRIMARY KEY,                     -- 充电宝的唯一标识，主键，自动递增
    location VARCHAR(100) NOT NULL,                        -- 充电宝的具体位置描述，最大长度100，不允许为空
    latitude DECIMAL(10, 6),                               -- 充电宝所在位置的纬度（精确到6位小数）
    longitude DECIMAL(10, 6),                              -- 充电宝所在位置的经度（精确到6位小数）
    battery_level TINYINT UNSIGNED NOT NULL,               -- 充电宝剩余电量（百分比，范围0-100）
    status ENUM('available', 'rented', 'maintenance') NOT NULL, -- 充电宝状态，可用值包括：可用、租赁中、维护中
    price_per_hour DECIMAL(5, 2) NOT NULL,                 -- 租赁价格（每小时），最多5位数字，小数点后两位
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,         -- 充电宝信息创建时间，默认为当前时间
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP -- 充电宝信息最后更新时间，每次更新会自动修改
);

-- 创建订单表
-- 该表用于记录用户的租赁订单信息
CREATE TABLE IF NOT EXISTS orders (
    id INT AUTO_INCREMENT PRIMARY KEY,                     -- 订单的唯一标识，主键，自动递增
    user_id INT NOT NULL,                                  -- 用户的唯一标识，外键，引用 users 表的 id
    powerbank_id INT NOT NULL,                             -- 充电宝的唯一标识，外键，引用 powerbanks 表的 id
    start_time DATETIME NOT NULL,                          -- 租赁开始时间，不允许为空
    end_time DATETIME DEFAULT NULL,                        -- 租赁结束时间，可以为空（租赁进行中时为空）
    total_cost DECIMAL(10, 2) DEFAULT 0.00,                -- 租赁总费用，最多10位数字，小数点后两位
    status ENUM('active', 'completed', 'cancelled') NOT NULL, -- 订单状态，可选值：进行中、已完成、已取消
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,         -- 订单创建时间，默认为当前时间
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE, -- 用户外键，用户被删除时关联的订单会自动删除
    FOREIGN KEY (powerbank_id) REFERENCES powerbanks(id) ON DELETE CASCADE -- 充电宝外键，充电宝被删除时关联的订单会自动删除
);

-- 添加索引以优化查询
-- 在用户表的手机号字段上创建索引，用于快速查找用户
CREATE INDEX idx_user_phone ON users(phone);

-- 在充电宝表的状态字段上创建索引，用于快速筛选充电宝状态
CREATE INDEX idx_powerbank_status ON powerbanks(status);

-- 在订单表的状态字段上创建索引，用于快速筛选订单状态
CREATE INDEX idx_order_status ON orders(status);
