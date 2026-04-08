# TrailNote 项目当前进度记录

## 1. 当前阶段结论

当前项目已经从“需求整理与视觉方案阶段”推进到“可运行 Web 移动端原型阶段”，可以作为后续正式开发的基础参考与交互验证载体。

本地已有两类主要成果：

- UI 设计文档与静态设计图
- 可运行的 Web 移动端原型
- 正式开发工程骨架与本地记忆体系

## 2. 已完成内容

### 2.1 产品与 UI 设计文档

已完成完整的移动端 UI 设计规范文档：

- [`HikeShare_UI_Design_Spec.md`](/Volumes/ssd/CodeSpaces/out_ui_design/HikeShare_UI_Design_Spec.md)

内容覆盖：

- 产品定位
- 信息架构
- 页面规划
- 统一视觉风格
- 组件规范
- 交互规则
- iOS / Android 共用设计语言

### 2.2 静态页面设计图

已生成完整高保真设计图并放在：

- [`ui/export`](/Volumes/ssd/CodeSpaces/out_ui_design/ui/export)

当前包含：

- 核心业务页面
- 认证页
- 发现 / 搜索 / 详情 / 记录 / 个人中心页面
- 设计系统页
- 组件状态页
- 空态 / 异常页
- 动效与无障碍说明页
- 研发 handoff 页

### 2.3 Design Token 与主题样式

已提取可复用设计 token：

- [`prototype/tokens.json`](/Volumes/ssd/CodeSpaces/out_ui_design/prototype/tokens.json)

已整理共享主题样式：

- [`prototype/theme.css`](/Volumes/ssd/CodeSpaces/out_ui_design/prototype/theme.css)

当前 token 已包含：

- 颜色
- 字体
- 间距
- 圆角
- 阴影
- 动效
- 组件尺寸

### 2.4 可运行 Web 移动端原型

原型入口：

- [`prototype/index.html`](/Volumes/ssd/CodeSpaces/out_ui_design/prototype/index.html)

交互主逻辑：

- [`prototype/src/app.js`](/Volumes/ssd/CodeSpaces/out_ui_design/prototype/src/app.js)

原型已具备：

- 页面切换
- 底部导航
- 页面抽屉切换器
- 表单输入与校验
- 收藏切换
- 弹层系统
- toast 反馈
- 轨迹记录状态机
- 评论发布反馈
- 个人资料编辑页

### 2.5 正式开发工程基线

已建立正式工程目录：

- [`clients/h5-web`](/Volumes/ssd/CodeSpaces/out_ui_design/clients/h5-web)
- [`clients/ios-native`](/Volumes/ssd/CodeSpaces/out_ui_design/clients/ios-native)
- [`clients/android-native`](/Volumes/ssd/CodeSpaces/out_ui_design/clients/android-native)
- [`clients/wechat-miniapp`](/Volumes/ssd/CodeSpaces/out_ui_design/clients/wechat-miniapp)
- [`server`](/Volumes/ssd/CodeSpaces/out_ui_design/server)
- [`memory`](/Volumes/ssd/CodeSpaces/out_ui_design/memory)

当前已打通的正式工程能力：

- [`clients/h5-web`](/Volumes/ssd/CodeSpaces/out_ui_design/clients/h5-web) 已升级为可构建的正式 H5 原型基线
- [`server`](/Volumes/ssd/CodeSpaces/out_ui_design/server) 已升级为 Spring Boot 多模块正式骨架
- [`vue-vben-admin/apps/web-ele`](/Volumes/ssd/CodeSpaces/out_ui_design/vue-vben-admin/apps/web-ele) 已完成生产构建
- MySQL 已完成本地初始化，账号密码为 `root/root`
- 项目本地 JDK 17 与 Maven 3.9.11 已接入 `.tools/`
- `scripts/java-env.sh` 已可稳定导出 `JAVA_HOME` 与 `MAVEN_HOME`
- 已创建业务库 `trailnote` 与管理库 `trailnote_admin`
- 已创建首批表：`tn_user`、`tn_user_follow`、`tn_route`、`tn_route_comment`、`tn_route_favorite`、`tn_waypoint`、`tn_waypoint_media`、`tn_admin_user`
- 业务端路线领域已切换到真实 MyBatis-Plus 实体、Mapper 与数据库查询
- 业务端个人资料、评论、收藏接口已切换到真实 MyBatis-Plus 实体、Mapper 与数据库查询
- 业务端搜索、地图结果、作者主页、关注接口已切换到真实 MyBatis-Plus 实体、Mapper 与数据库查询
- 业务端创作草稿、我的路线、草稿详情、提交发布接口已切换到真实 MyBatis-Plus 实体、Mapper 与数据库查询
- 管理端路线列表已切换为真实数据库查询
- 管理端概览统计、评论列表、路线状态流转已切换为真实数据库查询
- 管理端用户列表、用户详情、启用禁用、用户概览统计已切换为真实数据库查询
- H5 正式工程已接入真实发现流、路线详情、评论、收藏、关注、个人资料与收藏列表
- `server/` 多模块 `mvn test` 已通过，`common`、`api`、`admin-api` 当前均可编译测试
- `clients/h5-web` 当前 `npm run build` 已通过
- `vue-vben-admin/apps/web-ele` 当前概览页、用户管理页已接入真实管理接口，且 `typecheck`、`build` 已通过
- `vue-vben-admin/apps/web-ele` 当前路线管理页、评论管理页也已接入真实管理接口，且构建通过
- H5 正式工程当前搜索页、作者主页已接入真实接口并可与发现页/详情页联动
- `admin-api` 当前已补充路线审核详情接口，管理端可查看路线详情后执行状态流转
- H5 正式工程当前地图结果页、创作台已接入真实接口，浏览链路与创作链路都已具备基础可演示能力

## 3. 当前原型已实现的关键能力

### 3.1 认证链路

- 欢迎页
- 登录页
- 注册页
- 找回密码页
- 完善资料页

### 3.2 浏览链路

- 发现页
- 搜索页
- 地图结果页
- 路线详情页
- 点位详情页
- 作者主页
- 评论页

### 3.3 记录链路

- 记录准备页
- 轨迹记录中页
- 新增点位页
- 拍照 / 录像页
- 路线编辑页
- 发布预览页
- 发布成功页

### 3.4 个人中心链路

- 我的页
- 我的路线页
- 收藏夹页
- 设置页
- 编辑个人资料页

## 4. 当前已接入的真实交互

### 4.1 弹层

已实现：

- 收藏确认弹层
- 暂停记录弹层
- 结束记录弹层
- 评论成功弹层

### 4.2 表单状态

已实现：

- 输入值维护
- blur 后错误校验
- 错误文案提示
- 协议勾选
- 提交校验

### 4.3 收藏逻辑

已实现：

- 路线收藏切换，并已接入真实 `/api/routes/{routeId}/favorite`
- 作者关注切换，并已接入真实 `/api/authors/{authorId}/follow`
- 点位收藏切换
- 收藏夹联动展示，并已接入真实 `/api/me/favorites`

### 4.4 记录流程状态机

当前状态：

- `idle`
- `recording`
- `paused`
- `ended`

已联动内容：

- 状态文案
- 记录页按钮状态
- 记录数据展示
- 结束后进入整理页

### 4.5 H5 已接入真实 API 的页面

- 发现页：`/api/routes/featured`、`/api/routes`
- 路线详情页：`/api/routes/{routeId}`、`/api/routes/{routeId}/comments`
- 搜索页：`/api/search`
- 地图结果页：`/api/search/map`
- 作者主页：`/api/authors/{authorId}`
- 互动能力：`/api/routes/{routeId}/favorite`、`/api/authors/{authorId}/follow`
- 我的页：`/api/me`、`/api/me/favorites`
- 编辑资料：`PUT /api/me`
- 创作台：`/api/creator/routes`、`/api/creator/drafts/current`、`/api/creator/drafts/save`、`/api/creator/drafts/{routeId}/publish`

## 5. 最近一次重要调整

### 5.1 新增点位页

已新增“展开更多”按钮，用于展开更多点位类型。

当前默认展示：

- 观景点
- 补给点
- 危险提醒

展开后增加：

- 起点
- 终点
- 岔路口
- 休息点
- 营地点
- 拍摄机位

### 5.2 轨迹记录页

已移除轨迹记录中独立的“拍照”和“录像”按钮。

当前策略：

- 媒体采集统一进入“新增点位”流程
- 在新增点位页中提供“拍照添加”和“录像添加”入口

### 5.3 我的页

已补齐“编辑个人资料”页面，并接通：

- 昵称编辑
- 简介编辑
- 城市编辑
- 徒步等级编辑
- 真实资料保存与回写
- 经验等级编辑
- 兴趣标签选择

保存后会同步回写到“我的”页展示。

## 6. 当前关键文件说明

### 6.1 文档与设计稿

- [`HikeShare_UI_Design_Spec.md`](/Volumes/ssd/CodeSpaces/out_ui_design/HikeShare_UI_Design_Spec.md)
- [`ui/export`](/Volumes/ssd/CodeSpaces/out_ui_design/ui/export)

### 6.2 原型核心文件

- [`prototype/index.html`](/Volumes/ssd/CodeSpaces/out_ui_design/prototype/index.html)
- [`prototype/theme.css`](/Volumes/ssd/CodeSpaces/out_ui_design/prototype/theme.css)
- [`prototype/tokens.json`](/Volumes/ssd/CodeSpaces/out_ui_design/prototype/tokens.json)
- [`prototype/src/app.js`](/Volumes/ssd/CodeSpaces/out_ui_design/prototype/src/app.js)

### 6.3 后续迭代规范

- [`prototype/PROTOTYPE_CHANGE_GUIDE.md`](/Volumes/ssd/CodeSpaces/out_ui_design/prototype/PROTOTYPE_CHANGE_GUIDE.md)

### 6.4 后端环境与脚本

- [`scripts/java-env.sh`](/Volumes/ssd/CodeSpaces/out_ui_design/scripts/java-env.sh)
- [`scripts/run-server-tests.sh`](/Volumes/ssd/CodeSpaces/out_ui_design/scripts/run-server-tests.sh)

### 6.5 已落地业务接口

- 路线发现与详情：
  - `/api/routes/featured`
  - `/api/routes`
  - `/api/routes/{routeId}`
- 路线互动：
  - `/api/routes/{routeId}/favorite`
  - `/api/routes/{routeId}/comments`
- 当前用户：
  - `/api/me`
  - `/api/me/favorites`
- 搜索与地图：
  - `/api/search`
  - `/api/search/map`
- 作者主页：
  - `/api/authors/{authorId}`
  - `/api/authors/{authorId}/follow`
- 创作链路：
  - `/api/creator/routes`
  - `/api/creator/drafts/current`
  - `/api/creator/drafts/{routeId}`
  - `/api/creator/drafts/save`
  - `/api/creator/drafts/{routeId}/publish`

### 6.6 已落地后台接口

- 概览统计：
  - `/admin/dashboard/overview`
- 路线管理：
  - `/admin/routes`
  - `/admin/routes/{routeId}/status`
- 评论管理：
  - `/admin/comments`

## 7. 当前运行方式

项目根目录运行：

```bash
npm run prototype
```

浏览器访问：

- [http://127.0.0.1:4321/index.html](http://127.0.0.1:4321/index.html)

## 8. 正式开发前建议

建议接下来按以下顺序进入正式开发准备：

1. 等待 MySQL 与 JDK 安装完成，并补齐 Maven 可执行环境
2. 初始化正式数据库与后端运行配置
3. 继续将 `prototype/` 交互迁移到 [`clients/h5-web`](/Volumes/ssd/CodeSpaces/out_ui_design/clients/h5-web)
4. 基于现有服务端骨架扩展用户、路线、点位、媒体等领域模块
5. 在 iOS / Android / 微信小程序端按统一 token 与交互规则同步开工

## 9. 当前仍未进入正式开发级别的部分

- `openjdk@17` 与 `maven` 仍在安装流程中，服务端尚未完成本机编译验证
- Java 发行包下载速度较慢，已同时准备本地 `.tools/` 兜底接入脚本

以下内容当前仍属于原型阶段，正式开发前建议继续细化：

- 页面模块拆分尚未完成
- 真实数据结构与接口协议尚未定义
- 图片 / 视频上传流程仍为前端模拟
- 地图组件仍为视觉模拟，不是真实地图 SDK
- 表单与状态尚未接入持久化
- 设计 token 还未转成正式工程主题实现

## 10. 推荐的下一个工作项

如果下一步是“正式发开准备”，建议优先做下面三项：

1. 建立正式前端工程骨架
2. 迁移当前原型页面与交互逻辑
3. 定义页面级数据模型与接口占位

这份文件用于后续 handoff、需求继续迭代和正式开发准备。
