-- 地铁失物招领系统数据库初始化脚本
-- 创建数据库
CREATE DATABASE IF NOT EXISTS lost_in_subway DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE lost_in_subway;

-- 用户表
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密）',
    real_name VARCHAR(50) COMMENT '真实姓名',
    phone VARCHAR(20) COMMENT '手机号',
    email VARCHAR(100) COMMENT '邮箱',
    role ENUM('PASSENGER', 'ADMIN') NOT NULL DEFAULT 'PASSENGER' COMMENT '角色：乘客/管理员',
    status ENUM('ACTIVE', 'INACTIVE', 'BANNED') NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_role (role)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 地铁线路表
CREATE TABLE IF NOT EXISTS subway_lines (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    line_name VARCHAR(50) NOT NULL COMMENT '线路名称',
    line_number VARCHAR(20) NOT NULL COMMENT '线路编号',
    color VARCHAR(20) COMMENT '线路颜色',
    description TEXT COMMENT '线路描述',
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='地铁线路表';

-- 失物类型表
CREATE TABLE IF NOT EXISTS item_types (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    type_name VARCHAR(50) NOT NULL COMMENT '类型名称',
    description TEXT COMMENT '类型描述',
    icon VARCHAR(100) COMMENT '图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status ENUM('ACTIVE', 'INACTIVE') NOT NULL DEFAULT 'ACTIVE' COMMENT '状态',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='失物类型表';

-- 失物招领信息表
CREATE TABLE IF NOT EXISTS lost_found_items (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_type ENUM('LOST', 'FOUND') NOT NULL COMMENT '类型：失物/招领',
    title VARCHAR(100) NOT NULL COMMENT '标题',
    description TEXT NOT NULL COMMENT '详细描述',
    item_type_id BIGINT COMMENT '失物类型ID',
    subway_line_id BIGINT COMMENT '地铁线路ID',
    station_name VARCHAR(100) COMMENT '站点名称',
    lost_found_time DATETIME COMMENT '失物/招领时间',
    contact_name VARCHAR(50) COMMENT '联系人姓名',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    contact_email VARCHAR(100) COMMENT '联系邮箱',
    image_urls TEXT COMMENT '图片URL，多个用逗号分隔',
    publisher_id BIGINT NOT NULL COMMENT '发布者ID',
    status ENUM('PENDING', 'APPROVED', 'REJECTED', 'CLAIMED', 'RETURNED') NOT NULL DEFAULT 'PENDING' COMMENT '状态',
    reject_reason TEXT COMMENT '拒绝原因',
    admin_remark TEXT COMMENT '管理员备注',
    view_count INT DEFAULT 0 COMMENT '浏览次数',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_item_type (item_type),
    INDEX idx_status (status),
    INDEX idx_publisher (publisher_id),
    INDEX idx_created_at (created_at),
    FOREIGN KEY (item_type_id) REFERENCES item_types(id),
    FOREIGN KEY (subway_line_id) REFERENCES subway_lines(id),
    FOREIGN KEY (publisher_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='失物招领信息表';

-- 认领记录表
CREATE TABLE IF NOT EXISTS claims (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    item_id BIGINT NOT NULL COMMENT '失物招领信息ID',
    claimer_id BIGINT NOT NULL COMMENT '认领人ID',
    claim_reason TEXT NOT NULL COMMENT '认领理由',
    proof_images TEXT COMMENT '证明图片，多个用逗号分隔',
    status ENUM('PENDING', 'APPROVED', 'REJECTED') NOT NULL DEFAULT 'PENDING' COMMENT '状态',
    reject_reason TEXT COMMENT '拒绝原因',
    admin_remark TEXT COMMENT '管理员备注',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_item_id (item_id),
    INDEX idx_claimer_id (claimer_id),
    INDEX idx_status (status),
    FOREIGN KEY (item_id) REFERENCES lost_found_items(id),
    FOREIGN KEY (claimer_id) REFERENCES users(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='认领记录表';

-- 消息通知表
CREATE TABLE IF NOT EXISTS notifications (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    title VARCHAR(100) NOT NULL COMMENT '标题',
    content TEXT NOT NULL COMMENT '内容',
    type ENUM('SYSTEM', 'ITEM_STATUS', 'CLAIM_STATUS') NOT NULL DEFAULT 'SYSTEM' COMMENT '类型',
    is_read TINYINT(1) DEFAULT 0 COMMENT '是否已读',
    related_item_id BIGINT COMMENT '关联的失物招领ID',
    related_claim_id BIGINT COMMENT '关联的认领ID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_is_read (is_read),
    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (related_item_id) REFERENCES lost_found_items(id),
    FOREIGN KEY (related_claim_id) REFERENCES claims(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='消息通知表';

-- 初始化数据：地铁线路
INSERT INTO subway_lines (line_name, line_number, color, description) VALUES
('1号线', 'Line 1', '#FF0000', '东西主干线'),
('2号线', 'Line 2', '#00FF00', '南北主干线'),
('3号线', 'Line 3', '#0000FF', '环线'),
('4号线', 'Line 4', '#FFFF00', '郊区线'),
('5号线', 'Line 5', '#FF00FF', '机场线');

-- 初始化数据：失物类型
INSERT INTO item_types (type_name, description, icon, sort_order) VALUES
('证件类', '身份证、护照、驾驶证等', 'id-card', 1),
('钱包/现金', '钱包、现金、银行卡等', 'wallet', 2),
('电子产品', '手机、电脑、平板、耳机等', 'electronics', 3),
('衣物配饰', '衣服、帽子、围巾、眼镜等', 'clothing', 4),
('行李物品', '行李箱、背包、手提袋等', 'luggage', 5),
('文件资料', '文件、书籍、笔记本等', 'documents', 6),
('钥匙', '家门钥匙、车钥匙等', 'keys', 7),
('其他', '其他物品', 'other', 8);

-- 初始化数据：管理员账户（密码：admin123，需使用BCrypt加密）
-- 注意：实际部署时请修改密码
INSERT INTO users (username, password, real_name, phone, email, role, status) VALUES
('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '系统管理员', '13800138000', 'admin@subway.com', 'ADMIN', 'ACTIVE');

-- 初始化数据：测试乘客账户（密码：123456）
INSERT INTO users (username, password, real_name, phone, email, role, status) VALUES
('user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '张三', '13800138001', 'user1@example.com', 'PASSENGER', 'ACTIVE'),
('user2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iAt6Z5E', '李四', '13800138002', 'user2@example.com', 'PASSENGER', 'ACTIVE');
