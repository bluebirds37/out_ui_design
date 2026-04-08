## [LRN-20260326-001] best_practice

**Logged**: 2026-03-26T19:58:00+08:00
**Priority**: medium
**Status**: pending
**Area**: frontend

### Summary
When a target directory already contains placeholder files, generator CLIs like `create-vite` may cancel instead of scaffolding over the existing folder.

### Details
An attempt to run `npm create vite@latest clients/h5-web -- --template vue-ts` was canceled because the target directory was not empty. For repo bootstrapping where placeholder READMEs already exist, it is safer to scaffold into an empty directory or manually create the first-pass files.

### Suggested Action
- Prefer empty target directories for CLI scaffolds
- If placeholder files already exist, either remove them first or handcraft the initial skeleton

### Metadata
- Source: error
- Related Files: clients/h5-web/README.md
- Tags: vite, scaffolding, repo-bootstrap

---
## [LRN-20260326-002] best_practice

**Logged**: 2026-03-26T20:35:00+08:00
**Priority**: medium
**Status**: pending
**Area**: frontend

### Summary
对于 `vue-vben-admin` 这类源码型 monorepo，单个业务应用构建失败时，不应假设依赖包已经产出可消费的 `dist` 文件。

### Details
当前仓库内的 `@vben/web-ele` 构建在多个回合里分别暴露了 `@vben-core/design`、`@vben-core/form-ui`、`@vben-core/composables`、`@vben-core/icons`、`@vben-core/shared`、`@vben-core/typings` 的入口缺失问题。根因不是依赖安装失败，而是部分 workspace package 需要先执行 `build` 或 `stub`，生成本地构建产物后应用层才能正常打包。

### Suggested Action
- 保留一份管理端 bootstrap 脚本，集中预构建核心 workspace packages
- 后续再次遇到 `Failed to resolve entry for package ...` 时，优先检查对应 workspace package 是否已生成 `dist`

### Metadata
- Source: error
- Related Files: vue-vben-admin/package.json
- Tags: pnpm, monorepo, workspace, bootstrap

---
