# TrailNote 统一开发执行计划（共享基线版）

## 1. 文档目标与适用范围

本计划用于统一后续多端协同开发节奏，适用于：

- iOS（SwiftUI）
- Android（Jetpack Compose）
- H5（Vue 3 + TypeScript）
- 微信小程序（原生小程序）
- 业务后端（Spring Boot + MyBatis-Plus）
- 管理后端（admin-api）
- 后台前端（现有 `vue-vben-admin/apps/web-ele`）

本计划以当前仓库真实状态为依据，不将“规划项”表述为“已完成项”。

## 2. 当前真实基线复盘（2026-03-27）

### 2.1 已有实现（可复用）

- 设计真源已就位：`prototype/`、`prototype/tokens.json`、`prototype/theme.css`
- H5 正式工程已可构建并已接入多组真实接口：`clients/h5-web`
  - 当前仍以 `src/App.vue` 单体页面承载多视图，尚未完成按页面模块化拆分
- 业务后端已存在并可测试：`server/api`、`server/common`
  - 路线、互动（评论/收藏）、搜索、作者、个人资料、创作草稿链路已具备接口与数据库查询实现
- 管理后端已存在并可测试：`server/admin-api`
  - 已具备概览、用户、路线、评论等核心管理接口
- 后台前端已接入真实管理接口：`vue-vben-admin/apps/web-ele/src/api/trailnote-admin.ts`
  - 已有概览、用户、路线、评论管理页面联调能力
- 数据库初始化脚本可用：`server/sql/init.sql`

### 2.2 当前空白或仅占位

- `clients/ios-native`：仅 README，占位目录
- `clients/android-native`：仅 README，占位目录
- `clients/wechat-miniapp`：仅 README，占位目录
- 多端共享业务 SDK（接口层/实体层）尚未抽取为独立共享模块
- 多端统一命名与状态机约束尚未形成正式契约（本批文档补齐）

## 3. 技术栈结论（冻结）

- iOS：SwiftUI
- Android：Jetpack Compose
- H5：Vue 3 + TypeScript
- 微信小程序：原生小程序
- 业务后端：Spring Boot + MyBatis-Plus
- 后台前端：沿用现有 `vue-vben-admin`

说明：由于 iOS / Android / 小程序工程尚未创建，本阶段先冻结共享规则与骨架策略，再进入端侧工程创建。

## 4. 多端统一策略（必须遵守）

### 4.1 设计与交互真源

- 设计真源固定为：
  - `prototype/`
  - `prototype/tokens.json`
  - `prototype/theme.css`
- 任何端侧视觉或交互实现与原型冲突时，先回到真源核对，再改实现。

### 4.2 业务实现参考顺序

- H5 当前真实联调版（`clients/h5-web`）作为第一业务实现参考。
- iOS / Android / 小程序在业务语义、字段映射、状态机定义上优先对齐 H5 已验证链路。
- 后端接口调整优先保证“兼容 H5 线上联调行为”，再扩展其他端能力。

### 4.3 统一命名规则

#### 页面命名

- 页面 ID 使用 `kebab-case`，与路由语义一致，例如：`route-detail`、`author-profile`。
- 页面文件命名：
  - H5：统一采用目录 `kebab-case/index.vue`。
  - iOS：`XxxView.swift`。
  - Android：`XxxScreen.kt`。
  - 小程序：目录 `kebab-case`，页面 JSON/TS/WXML/WXSS同名。

#### 字段命名

- 接口 JSON 字段统一 `camelCase`。
- 枚举值统一 `UPPER_SNAKE_CASE`（如 `PENDING_REVIEW`）。
- 布尔字段统一前缀：`is/has/can` 或完成时态 `favorited/followed`，不得混用同义词。
- 时间字段统一后缀：`At`（如 `createdAt`、`updatedAt`）。
- 主键统一 `id`，外键统一 `{entity}Id`。

#### 状态命名

- 页面加载状态统一：`idle | loading | success | error`
- 记录链路状态统一：`idle | recording | paused | ended`
- 发布审核状态统一：`DRAFT | PENDING_REVIEW | PUBLISHED | REJECTED | ARCHIVED`
- 管理用户状态统一：`ACTIVE | DISABLED`

#### 交互命名

- 收藏切换动词统一 `toggleFavorite`
- 关注切换动词统一 `toggleFollow`
- 草稿保存统一 `saveDraft`
- 发布提交统一 `submitForReview`

## 5. 开发阶段划分

## 阶段 A：共享基线（当前阶段）

- 冻结多端 UI 契约、接口契约、命名与状态约束
- 明确 H5 为第一联调参考实现
- 产出统一执行文档（本次已落地）

## 阶段 B：客户端骨架

- 创建 iOS / Android / 小程序可运行工程骨架
- 接入统一主题 token（按端转换）
- 搭建路由/导航/基础网络层/错误处理骨架

## 阶段 C：共享业务模块

- 先落地浏览链路（发现/详情/搜索/作者）
- 再落地互动链路（评论/收藏/关注）
- 再落地个人中心与创作草稿链路

## 阶段 D：后端补齐

- 补齐接口一致性、参数校验、错误码、审计字段
- 补齐 admin-api 审核流细节（驳回原因、操作日志等）
- 补齐必要测试数据与集成测试

## 阶段 E：联调测试

- 多端对齐接口契约回归
- 弱网/异常态/空态统一验证
- 管理端与业务端数据链路联动验证

## 阶段 F：发布准备

- 多端构建与包体检查
- 监控、日志、告警与回滚预案
- 上线清单与发布窗口确认

## 6. 下一阶段建议优先级（建议立即执行）

1. 优先创建 iOS / Android / 小程序工程骨架，并接入统一 token 与基础导航。
2. 并行推进 H5 `App.vue` 单体拆分，形成可复用的页面级结构与 API 模块边界。
3. 统一输出“跨端枚举与错误码字典”到 `docs/`，避免后续端间分叉。
4. 后端补齐联调保障能力（字段稳定性、分页/排序一致性、错误码语义）。
5. 完成首轮“浏览 + 互动 + 创作草稿”跨端联调闭环后，再扩展高级特性。
