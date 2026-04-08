# Admin API

职责：

- 后台登录
- 权限
- 用户管理
- 内容审核
- 路线与点位管理
- 举报处理

当前状态：

- 已创建 Spring Boot 启动骨架
- 已提供：
  - `/admin/public/health`
  - `/admin/auth/login`
- 已扩展后台路线管理页接口：
  - `/admin/routes`
  - `/admin/routes/{routeId}/status`
  - `/admin/dashboard/overview`
  - `/admin/comments`
- 已扩展后台用户管理接口：
  - `/admin/users`
  - `/admin/users/{adminUserId}`
  - `/admin/users/{adminUserId}/status`
  - `/admin/users/summary`
