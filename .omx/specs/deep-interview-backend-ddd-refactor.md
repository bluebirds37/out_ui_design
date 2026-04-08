# Deep Interview Spec: Backend DDD Refactor

## Metadata

- Task slug: `backend-ddd-refactor`
- Profile: `standard`
- Rounds: `9`
- Final ambiguity: `0.194`
- Threshold: `0.200`
- Context type: `brownfield`
- Interview ID: `6b7f57fe-81e7-4248-a745-c31e2eab7608`
- Context snapshot: `.omx/context/backend-ddd-refactor-20260403T070505Z.md`
- Transcript summary: `.omx/interviews/backend-ddd-refactor-20260403T082717Z.md`

## Clarity Breakdown

| Dimension | Score |
| --- | --- |
| Intent Clarity | 0.85 |
| Outcome Clarity | 0.68 |
| Scope Clarity | 0.90 |
| Constraint Clarity | 0.82 |
| Success Criteria Clarity | 0.75 |
| Context Clarity | 0.80 |

Readiness gates:

- `Non-goals`: explicit
- `Decision Boundaries`: explicit
- `Pressure Pass`: complete

## Intent

本次后端重构的根本目的，是提升 TrailNote 后端在后续持续开发中的可维护性与可扩展性，避免现有以 `service + mapper/sql` 为主的实现方式在功能迭代增加后继续累积成难以维护的复杂结构。

## Desired Outcome

将整个后端统一迁移到 DDD 导向的开发模式，使业务开发后续围绕清晰的领域边界、应用服务编排、领域模型约束和基础设施实现协作展开，而不是继续在控制器、服务类和持久化细节中混合堆叠业务逻辑。

## In Scope

- 对 `server/` 下全部后端模块进行一次性架构迁移
- 将现有按业务域组织、但未形成 DDD 分层的代码，重组为清晰的 DDD 结构
- 为 `api` 与 `admin-api` 分别设计适合当前上下文的 DDD 落地方式
- 在不改变既有接口协议前提下，完成控制器、应用层、领域层、基础设施层的职责重组
- 允许根据需要重组包结构、抽取共享能力、选定 Repository/Gateway/Assembler/Factory 等模式
- 统一后续后端开发遵循 DDD 结构

## Out of Scope / Non-goals

- 不改接口协议
- 不新增中间件或消息队列
- 不做微服务拆分
- 不引入复杂 DDD 配套，如事件总线、CQRS、Event Sourcing

## Decision Boundaries

以下事项可由 OMX 在执行阶段自行决定，无需逐项回问：

- 新的 DDD 分层和包结构
- `api` 与 `admin-api` 是否采用不同深度的 DDD 落地方式
- 是否将部分公共能力抽到 `common` 或新的共享层
- Repository、Gateway、Assembler、Factory 等具体模式选择
- 各业务域的内部实现细节与迁移顺序

硬边界：

- 不得修改接口协议
- 不得引入新的中间件/消息队列
- 不得拆分为微服务
- 不得引入重型 DDD 配套模式

## Constraints

- 必须基于现有代码库演进，而不是直接重写整个后端
- 当前后端包含双上下文：业务端 `api` 与管理端 `admin-api`
- 当前实现技术栈已存在差异：`api` 为 MyBatis-Plus，`admin-api` 为 Spring JDBC
- 用户要求先完成一轮全后端一次性架构迁移，再继续后续功能开发
- 用户主要关注结构迁移完成，不将接口行为兼容性作为完成判定的必要条件

## Testable Acceptance Criteria

1. `server/` 下所有后端模块均完成新的 DDD 分层重组
2. 不再以“仅按业务域分目录但内部仍是 controller-service-mapper 混合实现”的方式作为主结构
3. 新结构可明确识别出职责边界，例如控制器适配层、应用编排层、领域模型/领域服务、基础设施持久化实现等
4. 后续新功能开发应以新的 DDD 结构为默认落点

## Success Criteria Notes

- 用户定义的主完成标准是“所有后端模块都按新的 DDD 分层重组完成”
- 用户明确接受一个高风险条件：
  - 即使迁移后出现接口行为回归，只要接口协议未改且结构迁移完成，仍视为本轮任务完成
- 推论：
  - 行为兼容性在执行阶段仍建议尽量验证，但不构成用户定义下的阻塞完成条件

## Assumptions Exposed And Resolutions

- Assumption: 可以用一个试点域先做 DDD，再逐步推广
  - Resolution: 被用户否定，要求全后端统一迁移
- Assumption: 迁移可能允许新旧结构短期并存
  - Resolution: 被用户否定，要求先做一次性全量迁移，再继续开发
- Assumption: 接口行为兼容性会是完成判定的底线
  - Resolution: 被用户否定，行为回归不阻止“结构迁移完成”的定义

## Pressure-pass Findings

- Revisited answer: 用户早期给出的 `route/creator` 试点偏好
- Pressure mechanism: 通过反问“如果只是结构分层但短期业务未复杂，是否仍值得”去挑战试点思路
- Updated conclusion: 用户更在意全局架构统一，而不是局部示范域成功

## Brownfield Evidence Vs Inference

Evidence:

- `server/` 为 Maven 多模块工程，含 `common`、`api`、`admin-api`
- `api` 使用 MyBatis-Plus，`admin-api` 使用 Spring JDBC
- 当前代码已按 `domain/*` 分目录
- `RouteQueryService`、`CreatorRouteService`、`AdminUserRepository` 等文件显示出强烈的事务脚本/数据访问直连式实现

Inference:

- 现有目录结构更接近“按业务域整理代码”，尚未形成完整 DDD 分层
- `route/creator` 的业务复杂度将天然更容易受益于领域建模，但用户最终要求已超出试点策略

## Technical Context Findings

- 现有服务端模块：
  - `server/common`
  - `server/api`
  - `server/admin-api`
- 典型现状模式：
  - Controller 直接依赖 Service/Gateway
  - Service 直接依赖 Mapper 并进行聚合查询、DTO 组装、事务流程
  - Repository/Gateway 命名存在，但语义尚不统一
- 未发现现成后端 DDD 设计文档

## Condensed Transcript

1. 重构动因：提高可维护性与可扩展性，避免后续变成屎山
2. 用户最初未给出具体失控场景，仅强调功能迭代后代码会混乱
3. 早期给出 `route/creator` 作为优先域
4. 经压力追问后明确：不是试点，而是全部后端统一迁移到 DDD
5. 迁移策略：先做一轮全后端一次性架构迁移，再继续开发
6. 非目标：不改接口协议、不加中间件、不拆微服务、不上重型 DDD 配套
7. 决策边界：架构方案与落地细节均可由 OMX 自主决定
8. 完成标准：所有后端模块完成 DDD 分层重组即可
9. 高风险补充：即使接口行为有回归，也仍算完成
