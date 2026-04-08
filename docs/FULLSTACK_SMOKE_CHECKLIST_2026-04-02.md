# 2026-04-02 全链路联调检查记录

## 1. 本次目标

- 启动业务后端、管理后端、H5 前端、管理前端
- 完成前后端本地联调
- 通过浏览器验证关键读写链路

## 2. 本地启动命令

### 2.1 业务后端

工作目录：`/Volumes/ssd/CodeSpaces/out_ui_design/server`

```bash
mvn -pl api spring-boot:run
```

服务地址：

- `http://127.0.0.1:8080`

健康检查：

- `http://127.0.0.1:8080/api/public/health`
- `http://127.0.0.1:8080/actuator/health`

### 2.2 管理后端

工作目录：`/Volumes/ssd/CodeSpaces/out_ui_design/server`

```bash
mvn -pl admin-api spring-boot:run
```

服务地址：

- `http://127.0.0.1:8081`

健康检查：

- `http://127.0.0.1:8081/actuator/health`

### 2.3 H5 前端

工作目录：`/Volumes/ssd/CodeSpaces/out_ui_design/clients/h5-web`

```bash
npm run dev:h5
```

服务地址：

- `http://127.0.0.1:4173`

说明：

- 当前 Vite 代理已可转发业务接口到 `http://127.0.0.1:8080`

### 2.4 管理前端

工作目录：`/Volumes/ssd/CodeSpaces/out_ui_design/vue-vben-admin`

首次修复本地 stub 路径后执行：

```bash
pnpm -r run stub --if-present
pnpm dev:ele
```

服务地址：

- `http://127.0.0.1:5777`

说明：

- 当前开发代理已将 `/trailnote-admin` 转发到 `http://127.0.0.1:8081`

## 3. 为本地联调做的管理前端适配

为保证 `vue-vben-admin` 能直接联调当前 `admin-api`，当前保留如下本地联调适配：

- 登录接口改为对接 `/admin/auth/login`
- 开发环境下统一通过 `/trailnote-admin` 代理访问管理后端
- 暂时移除登录页拖拽验证码依赖，避免本地真实登录被 demo 验证流程阻断
- 当前已补齐真实接口：
  - `/admin/auth/profile`
  - `/admin/auth/access-codes`
  - `/admin/auth/menus`
- `/admin/auth/profile`、`/admin/auth/access-codes`、`/admin/auth/menus` 当前均基于 `Authorization: Bearer <token>` 识别当前管理员
- 前端不再使用本地静态用户信息、权限码和菜单兜底，改为走后端真实返回
- 当前权限与菜单已落到数据库表驱动：
  - `tn_admin_role`
  - `tn_admin_permission`
  - `tn_admin_role_permission`
  - `tn_admin_menu`
  - `tn_admin_role_menu`
- 当前后台会话已落到 `tn_admin_session`，登录后会创建真实会话记录，后续接口通过 token 识别当前管理员

涉及文件：

- [`vue-vben-admin/apps/web-ele/src/api/core/auth.ts`](/Volumes/ssd/CodeSpaces/out_ui_design/vue-vben-admin/apps/web-ele/src/api/core/auth.ts)
- [`vue-vben-admin/apps/web-ele/src/api/core/user.ts`](/Volumes/ssd/CodeSpaces/out_ui_design/vue-vben-admin/apps/web-ele/src/api/core/user.ts)
- [`vue-vben-admin/apps/web-ele/src/api/core/menu.ts`](/Volumes/ssd/CodeSpaces/out_ui_design/vue-vben-admin/apps/web-ele/src/api/core/menu.ts)
- [`vue-vben-admin/apps/web-ele/src/api/trailnote-admin.ts`](/Volumes/ssd/CodeSpaces/out_ui_design/vue-vben-admin/apps/web-ele/src/api/trailnote-admin.ts)
- [`vue-vben-admin/apps/web-ele/vite.config.mts`](/Volumes/ssd/CodeSpaces/out_ui_design/vue-vben-admin/apps/web-ele/vite.config.mts)
- [`vue-vben-admin/apps/web-ele/src/views/_core/authentication/login.vue`](/Volumes/ssd/CodeSpaces/out_ui_design/vue-vben-admin/apps/web-ele/src/views/_core/authentication/login.vue)

## 4. 已验证的浏览链路

### 4.1 H5 业务前端

已验证页面：

- 欢迎页可正常打开
- 发现页可加载真实路线列表
- 路线详情页可加载真实路线内容
- 作者主页可加载真实作者内容

参考真实数据：

- 路线 `1001`：`北岭雾海穿越线`
- 路线 `1002`：`环湖松林轻徒步`
- 作者 `1002`

### 4.2 管理前端

已验证页面：

- 登录页可用 `admin` 账号登录
- 登录后可进入 `/analytics`
- 概览页展示真实统计数据
- 路线管理页展示真实路线数据
- 评论管理页展示真实评论数据
- 用户管理页展示真实用户数据

验证时观测到的概览数据：

- 总用户数：`2`
- 总路线数：`5`
- 已发布路线：`2`
- 待审核或草稿：`3`
- 总评论数：`7`
- 总收藏数：`2`

## 5. 已验证的写操作

### 5.1 管理端写操作

已验证并恢复原状态：

- 路线 `1003`：`DRAFT -> PENDING_REVIEW -> DRAFT`
- 用户 `support_admin`：`DISABLED -> ACTIVE -> DISABLED`

### 5.2 H5 业务端写操作

已验证并恢复原状态：

- 路线 `1001` 收藏切换后恢复
- 作者 `1002` 关注切换后恢复

已验证但未删除的新增数据：

- 路线 `1001` 新增评论：`biz-write-check-20260402201537`

当前对应状态：

- 路线 `1001` `favorited = true`
- 路线 `1001` `favoriteCount = 3401`
- 作者 `1002` `followed = false`
- 作者 `1002` `followerCount = 0`
- 路线 `1001` 评论总数当前为 `222`

## 6. 验证方式

- Shell 健康检查与接口调用
- 浏览器手工链路验证
- Playwright 自动化页面验证

相关脚本：

- [`scripts/fullstack-smoke-check.sh`](/Volumes/ssd/CodeSpaces/out_ui_design/scripts/fullstack-smoke-check.sh)

Playwright 产物目录：

- [`.playwright-cli`](/Volumes/ssd/CodeSpaces/out_ui_design/.playwright-cli)

## 7. 当前结论

- 四个核心服务均已可在本地启动
- H5 前端已可联调业务后端真实接口
- 管理前端已可通过本地代理联调管理后端真实接口
- 关键浏览链路与关键写操作已完成真实验证
- 当前唯一保留的联调副作用是一条业务评论测试数据，后续如需清理，需要补删除能力或手工清理数据库
