CREATE DATABASE IF NOT EXISTS trailnote DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
CREATE DATABASE IF NOT EXISTS trailnote_admin DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;

USE trailnote;

CREATE TABLE IF NOT EXISTS tn_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
  nickname VARCHAR(64) NOT NULL COMMENT '昵称',
  avatar_url VARCHAR(255) DEFAULT NULL COMMENT '头像地址',
  bio VARCHAR(255) DEFAULT NULL COMMENT '简介',
  city VARCHAR(64) DEFAULT NULL COMMENT '城市',
  level_label VARCHAR(64) DEFAULT NULL COMMENT '徒步等级标签',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='用户表';

CREATE TABLE IF NOT EXISTS tn_route (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '路线ID',
  user_id BIGINT NOT NULL COMMENT '作者用户ID',
  title VARCHAR(128) NOT NULL COMMENT '路线标题',
  cover_url VARCHAR(255) DEFAULT NULL COMMENT '封面图',
  description TEXT COMMENT '路线描述',
  difficulty VARCHAR(32) NOT NULL COMMENT '难度等级',
  distance_km DECIMAL(8,2) NOT NULL DEFAULT 0 COMMENT '路线长度公里',
  duration_minutes INT NOT NULL DEFAULT 0 COMMENT '耗时分钟',
  ascent_m INT NOT NULL DEFAULT 0 COMMENT '累计爬升米',
  max_altitude_m INT NOT NULL DEFAULT 0 COMMENT '最高海拔米',
  status VARCHAR(32) NOT NULL DEFAULT 'DRAFT' COMMENT '状态',
  favorite_count INT NOT NULL DEFAULT 0 COMMENT '收藏数',
  comment_count INT NOT NULL DEFAULT 0 COMMENT '评论数',
  tags VARCHAR(255) DEFAULT NULL COMMENT '标签，逗号分隔',
  published_at DATETIME DEFAULT NULL COMMENT '发布时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  KEY idx_route_user_id (user_id),
  KEY idx_route_status (status)
) COMMENT='路线表';

CREATE TABLE IF NOT EXISTS tn_route_comment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
  route_id BIGINT NOT NULL COMMENT '路线ID',
  user_id BIGINT NOT NULL COMMENT '评论用户ID',
  content VARCHAR(500) NOT NULL COMMENT '评论内容',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  KEY idx_comment_route_id (route_id),
  KEY idx_comment_user_id (user_id)
) COMMENT='路线评论表';

CREATE TABLE IF NOT EXISTS tn_route_favorite (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '收藏ID',
  route_id BIGINT NOT NULL COMMENT '路线ID',
  user_id BIGINT NOT NULL COMMENT '收藏用户ID',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY uk_route_user (route_id, user_id),
  KEY idx_favorite_user_id (user_id)
) COMMENT='路线收藏表';

CREATE TABLE IF NOT EXISTS tn_user_follow (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '关注ID',
  follower_user_id BIGINT NOT NULL COMMENT '发起关注用户ID',
  target_user_id BIGINT NOT NULL COMMENT '被关注用户ID',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY uk_follower_target (follower_user_id, target_user_id),
  KEY idx_follow_target_user_id (target_user_id)
) COMMENT='用户关注表';

CREATE TABLE IF NOT EXISTS tn_waypoint (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '点位ID',
  route_id BIGINT NOT NULL COMMENT '路线ID',
  title VARCHAR(128) NOT NULL COMMENT '点位标题',
  waypoint_type VARCHAR(32) NOT NULL COMMENT '点位类型',
  description VARCHAR(500) DEFAULT NULL COMMENT '点位说明',
  latitude DECIMAL(10,7) NOT NULL COMMENT '纬度',
  longitude DECIMAL(10,7) NOT NULL COMMENT '经度',
  altitude_m INT DEFAULT NULL COMMENT '海拔米',
  sort_order INT NOT NULL DEFAULT 0 COMMENT '路线内排序',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  KEY idx_waypoint_route_id (route_id)
) COMMENT='关键点位表';

CREATE TABLE IF NOT EXISTS tn_waypoint_media (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '媒体ID',
  waypoint_id BIGINT NOT NULL COMMENT '点位ID',
  media_type VARCHAR(16) NOT NULL COMMENT 'PHOTO/VIDEO',
  cover_url VARCHAR(255) DEFAULT NULL COMMENT '封面图',
  media_url VARCHAR(255) NOT NULL COMMENT '媒体地址',
  duration_seconds INT DEFAULT NULL COMMENT '视频时长秒',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  KEY idx_media_waypoint_id (waypoint_id)
) COMMENT='点位媒体表';

REPLACE INTO tn_user (id, nickname, avatar_url, bio, city, level_label)
VALUES
  (1001, '景野', 'https://static.trailnote.dev/avatar/jingye.jpg', '偏爱山脊线、湖边营地与日出路线记录', '上海', '轻中度徒步'),
  (1002, '阿屿', 'https://static.trailnote.dev/avatar/ayu.jpg', '喜欢轻徒步和湖边路线', '杭州', '新手友好');

REPLACE INTO tn_route (
  id, user_id, title, cover_url, description, difficulty, distance_km, duration_minutes,
  ascent_m, max_altitude_m, status, favorite_count, comment_count, tags, published_at
)
VALUES
  (
    1001, 1001, '北岭雾海穿越线', 'https://static.trailnote.dev/route/north-ridge-cover.jpg',
    '全程以山脊和林线过渡地形为主，分享者沿途补充了补给、风口和摄影机位信息，适合周末全天徒步。',
    'INTERMEDIATE', 11.40, 348, 780, 1428, 'PUBLISHED', 3400, 216, '山脊线,日出机位,雾海', '2026-03-24 07:20:00'
  ),
  (
    1002, 1002, '环湖松林轻徒步', 'https://static.trailnote.dev/route/lake-loop-cover.jpg',
    '适合周末半天出行的环湖线路，补给便利，适合新手。',
    'BEGINNER', 6.40, 180, 320, 942, 'PUBLISHED', 1876, 86, '湖边,轻徒步,新手友好', '2026-03-25 10:10:00'
  );

REPLACE INTO tn_waypoint (
  id, route_id, title, waypoint_type, description, latitude, longitude, altitude_m, sort_order
)
VALUES
  (2001, 1001, '山脊观景台', 'VIEWPOINT', '日出时逆光效果最好，风口较强，需准备防风层。', 30.2359100, 120.1045800, 1248, 1),
  (2002, 1001, '风口警示段', 'DANGER', '雨后石面偏滑，建议减速通过，不建议在此停留拍照。', 30.2381200, 120.1099300, 1320, 2),
  (2003, 1001, '林线补给点', 'SUPPLY', '可临时整理装备，手机信号较弱，建议提前缓存离线地图。', 30.2404700, 120.1136800, 1186, 3),
  (2101, 1002, '湖畔休息点', 'REST', '可短暂停留补水，日落时间适合拍摄湖面反光。', 30.2312100, 120.1187600, 826, 1);

REPLACE INTO tn_waypoint_media (
  id, waypoint_id, media_type, cover_url, media_url, duration_seconds
)
VALUES
  (3001, 2001, 'PHOTO', 'https://static.trailnote.dev/waypoint/2001-photo-cover.jpg', 'https://static.trailnote.dev/waypoint/2001-photo.jpg', NULL),
  (3002, 2001, 'VIDEO', 'https://static.trailnote.dev/waypoint/2001-video-cover.jpg', 'https://static.trailnote.dev/waypoint/2001-video.mp4', 18);

REPLACE INTO tn_route_comment (
  id, route_id, user_id, content, created_at, updated_at
)
VALUES
  (4001, 1001, 1002, '这条山脊线的风口提醒非常实用，按你标的节奏走完刚好赶上雾海。', '2026-03-24 09:20:00', '2026-03-24 09:20:00'),
  (4002, 1002, 1001, '环湖这段真的很适合带新手，补给点和休息点都很从容。', '2026-03-25 12:16:00', '2026-03-25 12:16:00');

REPLACE INTO tn_route_favorite (
  id, route_id, user_id, created_at
)
VALUES
  (5001, 1002, 1001, '2026-03-25 12:20:00'),
  (5002, 1001, 1002, '2026-03-24 09:25:00');

REPLACE INTO tn_user_follow (
  id, follower_user_id, target_user_id, created_at
)
VALUES
  (6001, 1001, 1002, '2026-03-25 12:18:00'),
  (6002, 1002, 1001, '2026-03-24 09:18:00');

USE trailnote_admin;

CREATE TABLE IF NOT EXISTS tn_admin_user (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '管理员ID',
  username VARCHAR(64) NOT NULL UNIQUE COMMENT '用户名',
  password_hash VARCHAR(255) NOT NULL COMMENT '密码哈希',
  role_code VARCHAR(64) NOT NULL COMMENT '角色编码',
  status VARCHAR(32) NOT NULL DEFAULT 'ACTIVE' COMMENT '启用状态',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='后台管理员表';

SET @admin_status_exists := (
  SELECT COUNT(*)
  FROM information_schema.COLUMNS
  WHERE TABLE_SCHEMA = 'trailnote_admin'
    AND TABLE_NAME = 'tn_admin_user'
    AND COLUMN_NAME = 'status'
);
SET @admin_status_sql := IF(
  @admin_status_exists = 0,
  'ALTER TABLE tn_admin_user ADD COLUMN status VARCHAR(32) NOT NULL DEFAULT ''ACTIVE'' COMMENT ''启用状态'' AFTER role_code',
  'SELECT 1'
);
PREPARE stmt_admin_status FROM @admin_status_sql;
EXECUTE stmt_admin_status;
DEALLOCATE PREPARE stmt_admin_status;

REPLACE INTO tn_admin_user (id, username, password_hash, role_code, status)
VALUES
  (1, 'root_admin', 'mocked-hash-root-admin', 'SUPER_ADMIN', 'ACTIVE'),
  (2, 'support_admin', 'mocked-hash-support-admin', 'OPERATOR', 'DISABLED');

CREATE TABLE IF NOT EXISTS tn_admin_role (
  role_code VARCHAR(64) PRIMARY KEY COMMENT '角色编码',
  role_name VARCHAR(64) NOT NULL COMMENT '角色名称',
  description VARCHAR(255) DEFAULT NULL COMMENT '角色说明',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='后台角色表';

CREATE TABLE IF NOT EXISTS tn_admin_permission (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '权限ID',
  permission_code VARCHAR(128) NOT NULL UNIQUE COMMENT '权限编码',
  permission_name VARCHAR(128) NOT NULL COMMENT '权限名称',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='后台权限表';

CREATE TABLE IF NOT EXISTS tn_admin_role_permission (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色权限关系ID',
  role_code VARCHAR(64) NOT NULL COMMENT '角色编码',
  permission_id BIGINT NOT NULL COMMENT '权限ID',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY uk_admin_role_permission (role_code, permission_id)
) COMMENT='后台角色权限关系表';

CREATE TABLE IF NOT EXISTS tn_admin_menu (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '菜单ID',
  parent_id BIGINT DEFAULT NULL COMMENT '父菜单ID',
  route_name VARCHAR(128) NOT NULL COMMENT '前端路由名称',
  route_path VARCHAR(255) NOT NULL COMMENT '前端路由路径',
  component VARCHAR(255) NOT NULL COMMENT '前端组件标识',
  redirect VARCHAR(255) DEFAULT NULL COMMENT '重定向路径',
  title VARCHAR(128) NOT NULL COMMENT '菜单标题',
  icon VARCHAR(128) DEFAULT NULL COMMENT '菜单图标',
  menu_order INT NOT NULL DEFAULT 0 COMMENT '菜单排序',
  affix_tab TINYINT(1) NOT NULL DEFAULT 0 COMMENT '是否固定标签页',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) COMMENT='后台菜单表';

CREATE TABLE IF NOT EXISTS tn_admin_role_menu (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '角色菜单关系ID',
  role_code VARCHAR(64) NOT NULL COMMENT '角色编码',
  menu_id BIGINT NOT NULL COMMENT '菜单ID',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  UNIQUE KEY uk_admin_role_menu (role_code, menu_id)
) COMMENT='后台角色菜单关系表';

CREATE TABLE IF NOT EXISTS tn_admin_session (
  id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '会话ID',
  admin_user_id BIGINT NOT NULL COMMENT '管理员ID',
  access_token VARCHAR(128) NOT NULL UNIQUE COMMENT '访问令牌',
  expires_at DATETIME NOT NULL COMMENT '过期时间',
  revoked_at DATETIME DEFAULT NULL COMMENT '注销时间',
  created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  KEY idx_admin_session_user_id (admin_user_id),
  KEY idx_admin_session_expires_at (expires_at)
) COMMENT='后台管理员会话表';

REPLACE INTO tn_admin_role (role_code, role_name, description)
VALUES
  ('SUPER_ADMIN', '超级管理员', '拥有后台所有管理能力'),
  ('OPERATOR', '内容运营', '负责内容审核与内容管理');

REPLACE INTO tn_admin_permission (id, permission_code, permission_name)
VALUES
  (1, 'dashboard:view', '查看概览'),
  (2, 'user:view', '查看用户'),
  (3, 'route:view', '查看路线'),
  (4, 'comment:view', '查看评论');

REPLACE INTO tn_admin_role_permission (role_code, permission_id)
VALUES
  ('SUPER_ADMIN', 1),
  ('SUPER_ADMIN', 2),
  ('SUPER_ADMIN', 3),
  ('SUPER_ADMIN', 4),
  ('OPERATOR', 1),
  ('OPERATOR', 3),
  ('OPERATOR', 4);

REPLACE INTO tn_admin_menu (id, parent_id, route_name, route_path, component, redirect, title, icon, menu_order, affix_tab)
VALUES
  (100, NULL, 'Dashboard', '/dashboard', 'BasicLayout', '/analytics', '仪表盘', 'lucide:layout-dashboard', -1, 0),
  (110, 100, 'Analytics', '/analytics', '/views/dashboard/analytics/index.vue', NULL, '分析页', 'lucide:area-chart', 0, 1),
  (200, NULL, 'TrailNoteContent', '/trailnote', 'BasicLayout', '/trailnote/routes', 'TrailNote 内容管理', 'lucide:route', 10, 0),
  (210, 200, 'TrailNoteRoutes', '/trailnote/routes', '/views/route/index.vue', NULL, '路线管理', 'lucide:map', 0, 0),
  (220, 200, 'TrailNoteComments', '/trailnote/comments', '/views/comment/index.vue', NULL, '评论管理', 'lucide:messages-square', 1, 0),
  (300, NULL, 'User', '/user', 'BasicLayout', '/user/index', '系统用户', 'mdi:user', 20, 0),
  (310, 300, 'UserIndex', '/user/index', '/views/user/index.vue', NULL, '用户管理', 'mdi:user', 0, 0);

REPLACE INTO tn_admin_role_menu (role_code, menu_id)
VALUES
  ('SUPER_ADMIN', 100),
  ('SUPER_ADMIN', 110),
  ('SUPER_ADMIN', 200),
  ('SUPER_ADMIN', 210),
  ('SUPER_ADMIN', 220),
  ('SUPER_ADMIN', 300),
  ('SUPER_ADMIN', 310),
  ('OPERATOR', 100),
  ('OPERATOR', 110),
  ('OPERATOR', 200),
  ('OPERATOR', 210),
  ('OPERATOR', 220);
