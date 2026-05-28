USE campus_market;

-- Password for all sample users is: 123456
INSERT INTO sys_user (id, username, password, nickname, phone, role, created_at, updated_at)
VALUES
    (1, 'admin', '$2a$10$Of0L6t3K8w4SkbnoGahsHeqQ.shPatKqYPp7tMGC/fwGsRnOg4yL2', 'Admin', '13800000000', 'ADMIN', NOW(), NOW()),
    (2, 'student1', '$2a$10$Of0L6t3K8w4SkbnoGahsHeqQ.shPatKqYPp7tMGC/fwGsRnOg4yL2', 'Student One', '13800000001', 'USER', NOW(), NOW()),
    (3, 'student2', '$2a$10$Of0L6t3K8w4SkbnoGahsHeqQ.shPatKqYPp7tMGC/fwGsRnOg4yL2', 'Student Two', '13800000002', 'USER', NOW(), NOW())
ON DUPLICATE KEY UPDATE nickname = VALUES(nickname), phone = VALUES(phone), role = VALUES(role), updated_at = NOW();

INSERT INTO market_category (id, name, sort, status, created_at, updated_at)
VALUES
    (1, 'Books', 1, 'ENABLE', NOW(), NOW()),
    (2, 'Electronics', 2, 'ENABLE', NOW(), NOW()),
    (3, 'Daily Goods', 3, 'ENABLE', NOW(), NOW())
ON DUPLICATE KEY UPDATE name = VALUES(name), sort = VALUES(sort), status = VALUES(status), updated_at = NOW();

INSERT INTO market_goods (id, seller_id, category_id, title, description, price, status, view_count, created_at, updated_at)
VALUES
    (1, 2, 1, 'Java Textbook', 'Used Java textbook for campus course practice.', 35.00, 'ON_SALE', 0, NOW(), NOW()),
    (2, 3, 2, 'Used Mechanical Keyboard', 'Blue switch keyboard in working condition.', 99.00, 'ON_SALE', 0, NOW(), NOW())
ON DUPLICATE KEY UPDATE title = VALUES(title), description = VALUES(description), price = VALUES(price), status = VALUES(status), updated_at = NOW();
