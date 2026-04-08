## Task Statement

通篇检查当前 TrailNote 后端 DDD 重构还存在哪些“未完成迁移内容”。

## Desired Outcome

形成一个明确的审查口径，区分：

- 已完成的结构迁移基线
- 仍未完成的迁移项
- 属于“已迁移但尚未深化”的 DDD 演进项

## Stated Solution

用户显式要求使用 `deep-interview`，先厘清“未完成迁移”的判断标准，再输出后续可执行的缺口审查结果。

## Probable Intent Hypothesis

- 避免把“已经完成的基础迁移”与“未来还能继续优化的 DDD 深化项”混为一谈
- 在继续后端重构前，确认当前还差哪些硬性迁移工作
- 识别哪些问题仍然阻碍“迁移完成”的说法成立

## Known Facts / Evidence

- 之前已按 DDD 重构目标推进了多轮后端迁移
- 当前后端结构基线已成立：
  - `server/api` 与 `server/admin-api` 采用领域垂直切片
  - 控制器位于 `interfaces/http`
  - `application` 层基本不再直接 import `infrastructure.persistence`
  - 控制器不再直接碰 `JdbcTemplate` / MyBatis Mapper / 内联 SQL
  - 旧的 `com.trailnote.api.domain.*` / `com.trailnote.admin.domain.*` 包引用已清理
- `admin/auth` 已从基础设施会话模型收口到领域会话模型：
  - `AdminSession`
  - `AdminAuthGateway`
- `creator` 已有：
  - 写聚合 `CreatorRouteDraft`
  - 仓储快照 `CreatorRouteDraftSnapshot`
  - application view `CreatorRouteDraftView`
  - 聚合内部成员 `CreatorDraftWaypoint` / `CreatorDraftMedia`
- `route` 已有：
  - 值对象 `RouteTagSet`
  - 生命周期枚举 `RouteLifecycleStatus`
  - 行为对象 `PublishedRoute`
  - 更通用的 `RouteAggregate`
- 全量验证当前通过：
  - `mvn -f server/pom.xml test`

## Constraints

- 用户之前明确接受的非目标仍有效：
  - 不改接口协议
  - 不新增中间件/消息队列
  - 不做微服务拆分
  - 不引入重型 DDD 配套（事件总线/CQRS/ES）
- 这次是 brownfield 审查，不是直接执行新一轮实现

## Unknowns / Open Questions

- “未完成迁移”到底按什么标准判断：
  - 结构迁移未收口的硬缺口
  - 仍未完全符合 DDD 目标的深化缺口
  - 两者都要，但分优先级
- 用户更想看到：
  - 阻塞“迁移完成”的问题清单
  - 未来继续深化的路线图
  - 或两层视图同时给出

## Decision-Boundary Unknowns

- 是否将“还可继续优化”的项也算作“未完成迁移”
- 是否只审查 `server/api` 与 `server/admin-api`
- 是否要把“查询型 slice 仍偏轻量 DDD”算作未完成缺口

## Likely Codebase Touchpoints

- `server/api/src/main/java/com/trailnote/api/creator/**`
- `server/api/src/main/java/com/trailnote/api/route/**`
- `server/api/src/main/java/com/trailnote/api/search/**`
- `server/api/src/main/java/com/trailnote/api/social/**`
- `server/admin-api/src/main/java/com/trailnote/admin/**`
- `server/docs/BACKEND_DDD_GUIDE.md`
