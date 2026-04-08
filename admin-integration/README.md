# Admin Integration Notes

当前已检测到后台管理前端脚手架目录：

- `/Volumes/ssd/CodeSpaces/out_ui_design/vue-vben-admin`

后续策略：

- 优先复用现有 monorepo 结构
- 评估以 `apps/web-ele` 作为后台管理前端主入口
- 在后端管理接口明确后，对接菜单、权限、页面与接口

建议执行顺序：

1. 使用 `pnpm` 安装 `vue-vben-admin` 依赖
2. 验证 `apps/web-ele` 可以启动
3. 确定后台菜单结构与 TrailNote 管理端业务模块
4. 在 `server/admin-api` 中同步建立管理端接口

当前补充：

- 已确认 `apps/web-ele` 可以完成生产构建
- 该脚手架属于源码型 monorepo，业务应用构建前需要优先构建部分本地 workspace packages
- 可直接使用脚本：
  - [`scripts/bootstrap-admin-web.sh`](/Volumes/ssd/CodeSpaces/out_ui_design/scripts/bootstrap-admin-web.sh)
