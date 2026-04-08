# TrailNote 共享接口与数据契约（多端）

## 1. 契约目标

本契约用于统一业务端（iOS/Android/H5/小程序）与管理端（admin-web）在接口、实体字段、枚举、缓存边界上的实现口径，避免联调阶段重复返工。

## 2. 当前已存在接口分组（基于仓库现状）

### 2.1 业务 API（`server/api`）

- 健康与认证
  - `GET /api/public/health`
  - `POST /api/auth/login`
- 路线浏览
  - `GET /api/routes/featured`
  - `GET /api/routes`
  - `GET /api/routes/{routeId}`
- 互动
  - `POST /api/routes/{routeId}/favorite`
  - `GET /api/routes/{routeId}/comments`
  - `POST /api/routes/{routeId}/comments`
- 搜索与地图
  - `GET /api/search`
  - `GET /api/search/map`
- 作者社交
  - `GET /api/authors/{authorId}`
  - `POST /api/authors/{authorId}/follow`
- 个人中心
  - `GET /api/me`
  - `PUT /api/me`
  - `GET /api/me/favorites`
- 创作链路
  - `GET /api/creator/routes`
  - `GET /api/creator/drafts/current`
  - `GET /api/creator/drafts/{routeId}`
  - `POST /api/creator/drafts/save`
  - `POST /api/creator/drafts/{routeId}/publish`

说明：

- 当前服务端现行接口路径仍使用 `/publish`。
- 多端领域动作命名统一使用 `submitForReview`，客户端服务层可将 `submitForReview` 映射到现有 `/publish` 接口。
- 后续若服务端升级路径命名，需要保持一段兼容期，避免多端联调同时中断。

### 2.2 管理 API（`server/admin-api`）

- 健康与认证
  - `GET /admin/public/health`
  - `POST /admin/auth/login`
- 仪表盘
  - `GET /admin/dashboard/overview`
- 用户管理
  - `GET /admin/users`
  - `GET /admin/users/{adminUserId}`
  - `GET /admin/users/summary`
  - `POST /admin/users/{adminUserId}/status`
- 路线管理
  - `GET /admin/routes`
  - `GET /admin/routes/{routeId}`
  - `POST /admin/routes/{routeId}/status`
- 评论管理
  - `GET /admin/comments`

## 3. 核心实体字段建议（多端共用）

说明：以下为当前实现 + 后续扩展的统一字段基线，命名以 `camelCase` 为准。

### 3.1 RouteSummary

- `id`
- `title`
- `coverUrl`
- `authorId`
- `authorName`
- `difficulty`
- `distanceKm`
- `durationMinutes`
- `ascentM`
- `waypointCount`
- `favoriteCount`
- `tags: string[]`

### 3.2 RouteDetail

- 继承 `RouteSummary` 关键字段
- `description`
- `maxAltitudeM`
- `commentCount`
- `favorited`
- `waypoints: Waypoint[]`

### 3.3 Waypoint

- `id`
- `routeId`（详情场景可选）
- `title`
- `waypointType`
- `description`
- `latitude`
- `longitude`
- `altitudeM`
- `sortOrder`
- `mediaList`

### 3.4 Comment

- `id`
- `routeId`
- `userId`
- `authorName`
- `authorAvatarUrl`
- `content`
- `isMine`
- `createdAt`

### 3.5 Profile（我的）

- `id`
- `nickname`
- `avatarUrl`
- `bio`
- `city`
- `levelLabel`
- `publishedRouteCount`
- `favoriteRouteCount`

### 3.6 AuthorProfile（作者）

- `id`
- `nickname`
- `avatarUrl`
- `bio`
- `city`
- `levelLabel`
- `followerCount`
- `followingCount`
- `publishedRouteCount`
- `followed`
- `routes: RouteSummary[]`

### 3.7 Draft（创作草稿）

- `id`
- `title`
- `coverUrl`
- `description`
- `difficulty`
- `distanceKm`
- `durationMinutes`
- `ascentM`
- `maxAltitudeM`
- `status`
- `tags: string[]`
- `waypoints: Waypoint[]`
- `updatedAt`

### 3.8 AdminRoute

- `id`
- `title`
- `authorName`
- `status`
- `difficulty`
- `distanceKm`
- `durationMinutes`
- `ascentM`
- `maxAltitudeM`
- `waypointCount`
- `favoriteCount`
- `commentCount`
- `tags`（当前 admin 详情为逗号分隔字符串，建议后续升级为数组）
- `publishedAt`
- `updatedAt`

### 3.9 AdminUser

- `id`
- `username`
- `roleCode`
- `status`
- `createdAt`
- `updatedAt`

## 4. 命名与枚举约定

### 4.1 命名规则

- 接口路径：资源复数 + 语义动作，例如 `/routes/{id}/favorite`
- JSON 字段：`camelCase`
- 时间字段：统一 `*At`
- 主键：`id`
- 外键：`{entity}Id`
- 分页：`page`、`pageSize`、`total`、`records`

### 4.2 枚举规则（当前已使用）

- 路线难度：`BEGINNER | INTERMEDIATE | ADVANCED`
- 点位类型：`VIEWPOINT | SUPPLY | DANGER | TRAILHEAD | ENDPOINT | FORK | REST | CAMP | PHOTO_SPOT`
- 媒体类型：`PHOTO | VIDEO`
- 路线状态：`DRAFT | PENDING_REVIEW | PUBLISHED | REJECTED | ARCHIVED`
- 管理用户状态：`ACTIVE | DISABLED`

### 4.3 响应包结构

- 统一响应壳：`ApiResponse<T>`
  - `success`
  - `code`
  - `message`
  - `data`
- 分页响应：`PageResponse<T>`
  - `records`
  - `total`
  - `page`
  - `pageSize`

## 5. 本地缓存 / SQLite 职责边界

### 5.1 必须落本地（允许离线）

- 记录链路临时状态：`idle/recording/paused/ended`
- 创作草稿（含点位草稿、媒体上传任务元信息）
- 页面级最近浏览缓存（可设置过期）
- 失败重试队列（仅记录链路同步、草稿保存、媒体上传等允许离线恢复的动作）

### 5.2 不应落本地主库

- 权威业务关系数据（关注关系、收藏计数、审核状态终态）
- 后台管理数据
- 长期一致性要求高且需服务端仲裁的数据
- 评论发布离线队列

### 5.3 一致性原则

- 服务端数据为最终真相（Source of Truth）
- 本地缓存仅做性能与离线兜底
- 网络恢复后以“服务端最新状态”覆盖本地冲突，并保留用户未提交草稿

## 6. 当前已知差异与后续收敛建议

- 业务端现有实现中仍可能看到 `type` 字段，后续客户端适配层统一映射为契约字段 `waypointType`，新增接口不再输出 `type`。
- admin 路线详情中的 `tags` 当前为字符串，建议升级为 `string[]` 与业务端对齐。
- 业务端现有实现中仍可能看到 `mine` 字段，后续客户端适配层统一映射为契约字段 `isMine`，新增接口不再新增 `mine`。
- 各端尚未建立共享 SDK，建议在下一阶段抽离统一 `contracts` 包（类型 + 枚举 + 错误码）。
