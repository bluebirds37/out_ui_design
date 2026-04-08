# Deep Interview Transcript Summary

- Task slug: `backend-ddd-refactor`
- Profile: `standard`
- Context type: `brownfield`
- Interview ID: `6b7f57fe-81e7-4248-a745-c31e2eab7608`
- Started from context snapshot: `.omx/context/backend-ddd-refactor-20260403T070505Z.md`
- Final ambiguity: `0.194`
- Threshold: `0.200`

## Condensed Findings

- 用户推动本次重构的核心原因是提升后端代码的可维护性与可扩展性，避免后续功能迭代把现有结构继续堆成难以维护的“屎山”
- 用户明确要求 DDD 不是试点策略，而是整个后端统一采用的开发范式
- 用户选择在继续新功能开发前，先做一轮全后端一次性架构迁移
- 非目标已明确：
  - 不改接口协议
  - 不新增中间件/消息队列
  - 不做微服务拆分
  - 不引入复杂 DDD 配套，如事件总线、CQRS、ES
- 决策边界已明确：新的 DDD 分层、包结构、共享层抽取方式、`api`/`admin-api` 的落地深度、Repository/Gateway/Assembler/Factory 等模式均可由 OMX 自行决定
- 用户定义的主要完成标准是：所有后端模块都完成新的 DDD 分层重组
- 用户进一步确认：即使迁移后接口行为出现回归，只要协议不变且结构迁移完成，也仍然算完成

## Brownfield Evidence

- `server/` 是 Maven 多模块工程，包含 `common`、`api`、`admin-api`
- `api` 使用 MyBatis-Plus；`admin-api` 使用 Spring JDBC
- 代码已按业务域分目录，但仍主要是 `Controller -> Service -> Mapper/JdbcTemplate` 的实现模式
- `RouteQueryService` 直接编排多个 Mapper 查询与 DTO/model 组装
- `CreatorRouteService` 在单个 service 中承载草稿保存、waypoint/media 替换、发布流程
- `AdminUserRepository` 作为 Gateway 实现，直接承载 SQL 与 DTO 映射

## Pressure Pass

- Revisitation target: 早期用户曾选择 `route/creator` 作为首个试点域
- Stress question: 如果只是把 `route/creator` 做成 `application/domain/infrastructure` 结构，但业务规则短期并未变复杂，这次重构是否仍然值得
- User resolution: 用户否定试点思路，明确要求“不论什么功能模块始终需要迁移到 DDD，请全部迁移”
- Effect: 将任务从“领域试点重构”收敛为“全后端统一架构迁移”

## Round Log

1. Q: 为什么现在做 DDD 重构，最想解决什么根问题
   A: 提升可维护性和扩展性，避免后续屎山代码堆积
2. Q: 哪类具体后端变更场景最容易失控
   A: 后续功能迭代上来后代码混乱
3. Q: 如果只能选一个试点域，会选哪一块
   A: `route/creator`
4. Q: 如果只是目录分层而业务规则短期不复杂，是否仍值得
   A: 所有功能模块都需要迁移到 DDD，不纠结某一块
5. Q: 迁移节奏是全量一次性还是分阶段并存
   A: 先做一轮全后端一次性架构迁移后再继续开发
6. Q: 明确不想一起做的事情有哪些
   A: 不改接口协议、不新增中间件/消息队列、不做微服务拆分、不引入事件总线/CQRS/ES
7. Q: 哪些架构决策可由 OMX 自行决定
   A: 都可自行决定
8. Q: 什么算迁移完成
   A: 所有后端模块都按新的 DDD 分层重组完成
9. Q: 如果结构完成但接口行为回归，是否也算完成
   A: 也算完成
