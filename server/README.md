# Server Workspace

包含：

- `api/` 业务主后端
- `admin-api/` 后台管理后端
- `common/` 公共模块
- `docs/` 服务端文档

推荐技术栈：

- Spring Boot
- MyBatis-Plus
- MySQL
- Redis

当前已落地：

- Maven 多模块父工程
- `common` 公共响应与异常处理
- `api` 业务主后端启动类与基础健康检查
- `admin-api` 后台管理后端启动类与基础健康检查
- 本地 JDK 17 / Maven 3.9 已接入 `.tools/`
- `server/sql/init.sql` 已可初始化业务库与管理库
- `server/` 多模块 `mvn test` 当前通过

后续建议顺序：

1. 继续扩展用户、评论、收藏、记录草稿等真实领域模块
2. 为后台管理后端增加用户管理、内容审核与统计接口
3. 对接 `vue-vben-admin` 与移动端原型页面
4. 增加更细的接口测试与联调脚本

当前新增：

- 初始化 SQL 脚本：
  - [`server/sql/init.sql`](/Volumes/ssd/CodeSpaces/out_ui_design/server/sql/init.sql)
- 服务端测试脚本：
  - [`scripts/run-server-tests.sh`](/Volumes/ssd/CodeSpaces/out_ui_design/scripts/run-server-tests.sh)
