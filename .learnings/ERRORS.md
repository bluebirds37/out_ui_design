## [ERR-20260326-001] brew_install_mysql_and_openjdk

**Logged**: 2026-03-26T19:58:00+08:00
**Priority**: high
**Status**: pending
**Area**: infra

### Summary
Homebrew installation commands for `mysql` and `openjdk@17` were started in long-running sessions but did not leave a confirmed installed state when checked from a fresh shell.

### Error
```text
brew list --versions mysql openjdk@17
```

returned non-zero after the initial install sessions had already been running.

### Context
- Command attempted:
  - `brew list mysql >/dev/null 2>&1 || brew install mysql`
  - `brew list openjdk@17 >/dev/null 2>&1 || brew install openjdk@17`
- Environment:
  - macOS with Homebrew 5.1.1
- Result:
  - New shell could not confirm either package as installed yet

### Suggested Fix
- Re-run each package installation as an isolated command and wait for definitive completion output
- After install, verify with:
  - `brew list --versions mysql`
  - `brew list --versions openjdk@17`
  - `mysql --version`
  - `/opt/homebrew/opt/openjdk@17/bin/java -version`

### Metadata
- Reproducible: unknown
- Related Files: memory/ENVIRONMENT.md

---
## [ERR-20260327-008] sql_init_existing_table_schema_drift

**Logged**: 2026-03-27T12:36:00+08:00
**Priority**: medium
**Status**: resolved
**Area**: backend

### Summary
当初始化脚本给已存在的 `tn_admin_user` 增加新列时，单纯依赖 `CREATE TABLE IF NOT EXISTS` 会导致旧库结构未升级，后续 `REPLACE INTO ... status` 直接失败。

### Error
```text
ERROR 1054 (42S22): Unknown column 'status' in 'field list'
```

### Context
- Command/operation attempted: `mysql -uroot -proot < server/sql/init.sql`
- Existing database: `trailnote_admin`
- Cause: 旧表已存在但缺少新增的 `status` 字段

### Suggested Fix
- 对初始化脚本中的结构演进使用显式增量迁移逻辑，而不是只依赖 `CREATE TABLE IF NOT EXISTS`
- 在当前项目中，使用 `information_schema.columns` + `PREPARE/EXECUTE` 动态执行补列 SQL

### Metadata
- Reproducible: yes
- Related Files: server/sql/init.sql

---
## [ERR-20260326-006] zsh_glob_empty_match

**Logged**: 2026-03-26T23:36:00+08:00
**Priority**: medium
**Status**: resolved
**Area**: infra

### Summary
在 `zsh` 下使用 `rm -rf .tools/*` 清空空目录时，会因为未匹配到任何文件直接报错并中断工具链接入。

### Error
```text
zsh: no matches found: .tools/*
```

### Context
- Command/operation attempted: 本地 JDK / Maven 解压前清空 `.tools/`
- Environment details: shell 为 `zsh`，`.tools/` 初始为空目录

### Suggested Fix
- 使用 `find .tools -mindepth 1 -maxdepth 1 -exec rm -rf {} +`
- 避免在 `zsh` 中对可能为空的目录使用未保护的 `*` 通配删除

### Metadata
- Reproducible: yes
- Related Files: scripts/java-env.sh

---
## [ERR-20260326-007] mybatis_plus_pagination_split_package

**Logged**: 2026-03-26T23:41:00+08:00
**Priority**: medium
**Status**: resolved
**Area**: backend

### Summary
MyBatis-Plus 3.5.10.1 中分页拦截器 `PaginationInnerInterceptor` 不再由 `mybatis-plus-extension` 单独提供，缺失 `mybatis-plus-jsqlparser` 会导致编译失败。

### Error
```text
找不到符号: 类 PaginationInnerInterceptor
```

### Context
- Command/operation attempted: `cd server && mvn test`
- Affected module: `server/api`
- Related code: `server/api/src/main/java/com/trailnote/api/config/MybatisPlusConfig.java`

### Suggested Fix
- 在使用分页拦截器的模块显式添加 `com.baomidou:mybatis-plus-jsqlparser`
- 保留 `MybatisPlusInterceptor` 配置不变，仅补齐拆分后的依赖包

### Metadata
- Reproducible: yes
- Related Files: server/api/pom.xml, server/api/src/main/java/com/trailnote/api/config/MybatisPlusConfig.java

---

## [ERR-20260326-002] vue_vben_admin_build_missing_design_entry

**Logged**: 2026-03-26T19:58:00+08:00
**Priority**: high
**Status**: resolved
**Area**: frontend

### Summary
The `vue-vben-admin` admin frontend install succeeded, but building `@vben/web-ele` failed due to unresolved package entry for `@vben-core/design`.

### Error
```text
[commonjs--resolver] Failed to resolve entry for package "@vben-core/design". The package may have incorrect main/module/exports specified in its package.json.
```

### Context
- Command attempted:
  - `pnpm -C vue-vben-admin -F @vben/web-ele run build`
- Prior step:
  - `pnpm install` completed successfully in `vue-vben-admin`
- Likely cause:
  - workspace package build / stub state incomplete for `@vben-core/design`

### Suggested Fix
- Inspect `packages/` workspace for `@vben-core/design`
- Check whether root workspace requires a full `pnpm build`, `pnpm run stub`, or a specific package bootstrap step before app build
- Verify package `exports` and dist generation

### Metadata
- Reproducible: yes
- Related Files: admin-integration/README.md

---
## [ERR-20260326-003] brew_install_lock_contention

**Logged**: 2026-03-26T20:05:00+08:00
**Priority**: medium
**Status**: pending
**Area**: infra

### Summary
Repeated `brew install` retries for `mysql` and `openjdk@17` hit Homebrew cache lock contention because earlier install sessions were still active.

### Error
```text
Error: A `brew install mysql` process has already locked ...
Error: A `brew install openjdk@17` process has already locked ...
```

### Context
- A new shell retried install before the original long-running install finished
- Homebrew used `.incomplete` download lock files under `~/Library/Caches/Homebrew/downloads`

### Suggested Fix
- Check active brew processes with `ps aux | rg 'brew install'`
- Wait for original installs to finish, or terminate the stale process before retrying
- Avoid issuing parallel retries for the same formula

### Metadata
- Reproducible: yes
- Related Files: memory/ENVIRONMENT.md

---
## [ERR-20260326-004] vue_vben_admin_workspace_dist_chain

**Logged**: 2026-03-26T20:35:00+08:00
**Priority**: medium
**Status**: resolved
**Area**: frontend

### Summary
`vue-vben-admin` 的 `@vben/web-ele` 在当前工作区内依赖多个尚未产出 `dist` 的本地 package，导致构建会按链路连续报缺失入口。

### Error
```text
[commonjs--resolver] Failed to resolve entry for package "@vben-core/composables"
[commonjs--resolver] Failed to resolve entry for package "@vben-core/icons"
```

### Context
- 直接执行：
  - `pnpm -C vue-vben-admin -F @vben/web-ele run build`
- 之后依次补齐：
  - `@vben-core/composables`
  - `@vben-core/icons`
  - `@vben-core/shared`
  - `@vben-core/typings`
- 最终 `@vben/web-ele` 构建成功

### Suggested Fix
- 将该脚手架视为源码型 monorepo，而非完全预编译的交付包
- 业务应用构建前，优先预构建依赖的核心 workspace packages
- 后续可补一份项目级 bootstrap 脚本统一处理

### Metadata
- Reproducible: yes
- Related Files: vue-vben-admin/apps/web-ele/package.json
- See Also: ERR-20260326-002

---
## [ERR-20260326-005] brew_homebrew_api_cache_state

**Logged**: 2026-03-26T21:42:00+08:00
**Priority**: medium
**Status**: pending
**Area**: infra

### Summary
在清理悬挂的 `openjdk@17` 安装进程后，Homebrew 一度因为本地 API 缓存目录状态异常，导致新的安装命令直接失败。

### Error
```text
Error: No such file or directory @ dir_s_rmdir - /Users/blue/Library/Caches/Homebrew/api/formula.jws.json
```

### Context
- 清理旧的 `brew install openjdk@17` 与相关 `curl` 进程后
- 重新执行：
  - `brew install openjdk@17`
- 随后通过：
  - `mkdir -p ~/Library/Caches/Homebrew/api`
  - `brew update --force --quiet`
  修复 Homebrew API 缓存目录状态

### Suggested Fix
- 当 Homebrew 在长时间悬挂下载后再次安装报 API 缓存异常时，优先检查 `~/Library/Caches/Homebrew/api`
- 必要时重建该目录并执行 `brew update --force --quiet`

### Metadata
- Reproducible: unknown
- Related Files: memory/ENVIRONMENT.md
- See Also: ERR-20260326-001, ERR-20260326-003

---

## [ERR-20260327-001] zsh_glob_recursive_read

**Logged**: 2026-03-27T11:42:16.639615+08:00
**Priority**: low
**Status**: resolved
**Area**: infra

### Summary
使用 `zsh` 读取多层级 Java 文件时，未匹配到的递归通配符会直接报错，中断一次只读检查命令。

### Error
```text
zsh: no matches found: server/admin-api/src/main/java/com/trailnote/admin/*/*/*.java
```

### Context
- Command/operation attempted: 批量读取 `admin-api` Java 源文件
- Environment details: shell 为 `zsh`，部分层级路径不存在

### Suggested Fix
统一改用 `find ... -type f` 读取文件，避免在 `zsh` 中依赖可能为空的递归 glob。

### Metadata
- Reproducible: yes
- Related Files: server/admin-api/src/main/java

---
