CREATE DATABASE IF NOT EXISTS campus_market DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE campus_market;

CREATE TABLE IF NOT EXISTS sys_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(64) NOT NULL UNIQUE,
    password VARCHAR(128) NOT NULL,
    nickname VARCHAR(64),
    phone VARCHAR(32),
    avatar_url VARCHAR(255),
    school VARCHAR(100),
    major VARCHAR(100),
    grade VARCHAR(32),
    bio VARCHAR(500),
    credit_score INT NOT NULL DEFAULT 100,
    role VARCHAR(20) NOT NULL DEFAULT 'USER',
    status VARCHAR(20) NOT NULL DEFAULT 'ENABLE',
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_user_role (role),
    INDEX idx_user_status (status)
);

DROP PROCEDURE IF EXISTS add_column_if_missing;
DELIMITER //
CREATE PROCEDURE add_column_if_missing(IN table_name_value VARCHAR(64), IN column_name_value VARCHAR(64), IN column_definition TEXT)
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns
        WHERE table_schema = DATABASE()
          AND table_name = table_name_value
          AND column_name = column_name_value
    ) THEN
        SET @ddl = CONCAT('ALTER TABLE ', table_name_value, ' ADD COLUMN ', column_name_value, ' ', column_definition);
        PREPARE stmt FROM @ddl;
        EXECUTE stmt;
        DEALLOCATE PREPARE stmt;
    END IF;
END//
DELIMITER ;

CALL add_column_if_missing('sys_user', 'avatar_url', 'VARCHAR(255) AFTER phone');
CALL add_column_if_missing('sys_user', 'school', 'VARCHAR(100) AFTER avatar_url');
CALL add_column_if_missing('sys_user', 'major', 'VARCHAR(100) AFTER school');
CALL add_column_if_missing('sys_user', 'grade', 'VARCHAR(32) AFTER major');
CALL add_column_if_missing('sys_user', 'bio', 'VARCHAR(500) AFTER grade');
CALL add_column_if_missing('sys_user', 'credit_score', 'INT NOT NULL DEFAULT 100 AFTER bio');
CALL add_column_if_missing('sys_user', 'status', 'VARCHAR(20) NOT NULL DEFAULT ''ENABLE'' AFTER role');

CREATE TABLE IF NOT EXISTS market_category (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(64) NOT NULL UNIQUE,
    sort INT NOT NULL DEFAULT 0,
    status VARCHAR(20) NOT NULL DEFAULT 'ENABLE',
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_category_status_sort (status, sort)
);

CREATE TABLE IF NOT EXISTS market_goods (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    seller_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    title VARCHAR(128) NOT NULL,
    description TEXT,
    price DECIMAL(10,2) NOT NULL,
    condition_level VARCHAR(32) NOT NULL DEFAULT 'GOOD',
    trade_location VARCHAR(128),
    cover_image VARCHAR(255),
    status VARCHAR(32) NOT NULL,
    audit_reason VARCHAR(500),
    view_count INT NOT NULL DEFAULT 0,
    favorite_count INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_goods_status (status),
    INDEX idx_goods_category (category_id),
    INDEX idx_goods_seller (seller_id),
    INDEX idx_goods_title (title)
);

CALL add_column_if_missing('market_goods', 'condition_level', 'VARCHAR(32) NOT NULL DEFAULT ''GOOD'' AFTER price');
CALL add_column_if_missing('market_goods', 'trade_location', 'VARCHAR(128) AFTER condition_level');
CALL add_column_if_missing('market_goods', 'cover_image', 'VARCHAR(255) AFTER trade_location');
CALL add_column_if_missing('market_goods', 'audit_reason', 'VARCHAR(500) AFTER status');
CALL add_column_if_missing('market_goods', 'favorite_count', 'INT NOT NULL DEFAULT 0 AFTER view_count');

DROP PROCEDURE IF EXISTS add_column_if_missing;

CREATE TABLE IF NOT EXISTS market_goods_image (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    goods_id BIGINT NOT NULL,
    image_url VARCHAR(255) NOT NULL,
    sort INT NOT NULL DEFAULT 0,
    created_at DATETIME NOT NULL,
    INDEX idx_image_goods (goods_id)
);

CREATE TABLE IF NOT EXISTS market_order (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_no VARCHAR(64) NOT NULL UNIQUE,
    buyer_id BIGINT NOT NULL,
    seller_id BIGINT NOT NULL,
    goods_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    status VARCHAR(32) NOT NULL,
    created_at DATETIME NOT NULL,
    updated_at DATETIME NOT NULL,
    INDEX idx_order_buyer (buyer_id),
    INDEX idx_order_seller (seller_id),
    INDEX idx_order_goods (goods_id),
    INDEX idx_order_status (status)
);

CREATE TABLE IF NOT EXISTS payment_record (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    pay_no VARCHAR(64) NOT NULL UNIQUE,
    order_id BIGINT NOT NULL,
    amount DECIMAL(10,2) NOT NULL,
    pay_type VARCHAR(32) NOT NULL DEFAULT 'MOCK',
    pay_status VARCHAR(32) NOT NULL,
    paid_at DATETIME,
    created_at DATETIME NOT NULL,
    INDEX idx_payment_order (order_id)
);

CREATE TABLE IF NOT EXISTS order_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    order_id BIGINT NOT NULL,
    operator_id BIGINT NOT NULL,
    action VARCHAR(64) NOT NULL,
    from_status VARCHAR(32),
    to_status VARCHAR(32) NOT NULL,
    remark VARCHAR(500),
    created_at DATETIME NOT NULL,
    INDEX idx_order_log_order (order_id)
);

CREATE TABLE IF NOT EXISTS goods_audit_log (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    goods_id BIGINT NOT NULL,
    admin_id BIGINT NOT NULL,
    result VARCHAR(32) NOT NULL,
    reason VARCHAR(500),
    created_at DATETIME NOT NULL,
    INDEX idx_audit_goods (goods_id),
    INDEX idx_audit_admin (admin_id)
);
