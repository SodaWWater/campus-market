USE campus_market;

-- Sample password is 123456, stored as BCrypt.
INSERT INTO sys_user (id, username, password, nickname, phone, school, major, grade, credit_score, role, status, created_at, updated_at)
VALUES
    (1, 'admin', '$2a$10$RdH5wfAciszqgmr64vZ4SuCAh3RlqyQd/Fv.hGfYDvOziQJka1tze', '平台管理员', '13800000000', '天津理工大学', '软件工程', '2023', 100, 'ADMIN', 'ENABLE', NOW(), NOW()),
    (2, 'seller1', '$2a$10$RdH5wfAciszqgmr64vZ4SuCAh3RlqyQd/Fv.hGfYDvOziQJka1tze', '卖家一号', '13800000001', '天津理工大学', '软件工程', '2023', 100, 'USER', 'ENABLE', NOW(), NOW()),
    (3, 'buyer1', '$2a$10$RdH5wfAciszqgmr64vZ4SuCAh3RlqyQd/Fv.hGfYDvOziQJka1tze', '买家一号', '13800000002', '天津理工大学', '计算机科学', '2024', 100, 'USER', 'ENABLE', NOW(), NOW()),
    (4, 'buyer2', '$2a$10$RdH5wfAciszqgmr64vZ4SuCAh3RlqyQd/Fv.hGfYDvOziQJka1tze', '买家二号', '13800000003', '天津理工大学', '网络工程', '2024', 100, 'USER', 'ENABLE', NOW(), NOW())
ON DUPLICATE KEY UPDATE password = VALUES(password), nickname = VALUES(nickname), phone = VALUES(phone), role = VALUES(role), status = VALUES(status), updated_at = NOW();

INSERT INTO market_category (id, name, sort, status, created_at, updated_at)
VALUES
    (1, '教材书籍', 1, 'ENABLE', NOW(), NOW()),
    (2, '数码电子', 2, 'ENABLE', NOW(), NOW()),
    (3, '生活用品', 3, 'ENABLE', NOW(), NOW()),
    (4, '运动户外', 4, 'ENABLE', NOW(), NOW())
ON DUPLICATE KEY UPDATE name = VALUES(name), sort = VALUES(sort), status = VALUES(status), updated_at = NOW();

INSERT INTO market_goods (id, seller_id, category_id, title, description, price, condition_level, trade_location, cover_image, status, view_count, favorite_count, created_at, updated_at)
VALUES
    (1, 2, 1, 'Java 核心技术教材', '课程用书，八成新，有少量笔记。', 35.00, 'GOOD', '图书馆门口', NULL, 'ON_SALE', 0, 0, NOW(), NOW()),
    (2, 2, 2, '九成新机械键盘', '青轴机械键盘，自用一年，无故障。', 99.00, 'LIKE_NEW', '宿舍区 3 号楼', NULL, 'PENDING_AUDIT', 0, 0, NOW(), NOW())
ON DUPLICATE KEY UPDATE title = VALUES(title), description = VALUES(description), price = VALUES(price), status = VALUES(status), updated_at = NOW();
