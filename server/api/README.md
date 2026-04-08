# Business API

职责：

- 用户
- 路线
- 点位
- 媒体
- 评论
- 收藏
- 草稿

当前状态：

- 已创建 Spring Boot 启动骨架
- 已提供：
  - `/api/public/health`
  - `/api/auth/login`
- 已扩展真实业务域接口：
  - `/api/routes/featured`
  - `/api/routes`
  - `/api/routes/{routeId}`
  - `/api/routes/{routeId}/favorite`
  - `/api/routes/{routeId}/comments`
  - `/api/me`
  - `/api/me/favorites`
  - `/api/search`
  - `/api/search/map`
  - `/api/authors/{authorId}`
  - `/api/authors/{authorId}/follow`
  - `/api/creator/routes`
  - `/api/creator/drafts/current`
  - `/api/creator/drafts/{routeId}`
  - `/api/creator/drafts/save`
  - `/api/creator/drafts/{routeId}/publish`

当前数据源说明：

- 路线、点位、点位媒体：真实 MyBatis-Plus 查询
- 个人资料：真实 MyBatis-Plus 查询与更新
- 路线评论：真实 MyBatis-Plus 查询与写入
- 路线收藏：真实 MyBatis-Plus 查询与切换
- 搜索与地图结果：真实 MyBatis-Plus 查询
- 作者主页与关注：真实 MyBatis-Plus 查询与切换
- 创作草稿与发布流：真实 MyBatis-Plus 查询、保存与状态流转
- 当前用户：开发期使用固定演示用户 `1001`
