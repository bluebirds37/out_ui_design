## Task Statement

在现有 TrailNote 项目基础上，对后端进行代码重构，并在后续开发中落地 DDD（领域驱动设计）模式。

## Desired Outcome

形成一个可执行的后端重构需求定义，明确 DDD 落地目标、范围边界、优先域、演进方式、验收标准，以及 OMX 在执行阶段可自行决定的事项。

## Stated Solution

用户明确提出“后端代码重构”，并要求“在之前基础上实现 DDD 领域驱动模式开发”。

## Probable Intent Hypothesis

- 降低当前后端继续演进时的结构性负担
- 将已有“按 domain 分包但非真正 DDD”的实现演进为更清晰的领域边界
- 为后续功能开发建立更稳定的应用层、领域层、基础设施层协作方式
- 避免一次性推倒重来，倾向在现有系统上逐步演进

## Known Facts / Evidence

- 仓库是多端项目，后端位于 `server/`，为 Maven 多模块工程：`common`、`api`、`admin-api`
- 服务端使用 Java 17、Spring Boot 3.4.4
- `api` 模块使用 MyBatis-Plus；`admin-api` 模块使用 Spring JDBC
- 代码已按业务域拆分目录，例如：
  - `server/api/src/main/java/com/trailnote/api/domain/route`
  - `server/api/src/main/java/com/trailnote/api/domain/creator`
  - `server/api/src/main/java/com/trailnote/api/domain/social`
  - `server/admin-api/src/main/java/com/trailnote/admin/domain/user`
- 当前主要实现模式仍以 `Controller -> Service -> Mapper/JdbcTemplate` 为主
- 示例证据：
  - `RouteController` 直接依赖 `RouteQueryService`
  - `RouteQueryService` 直接组织 `RouteMapper` / `UserMapper` / `WaypointMapper` / `RouteFavoriteMapper`
  - `CreatorRouteService` 在单个 service 中承担保存草稿、替换 waypoint、发布等流程编排与持久化细节
  - `AdminUserController` 直接依赖 `AdminUserGateway`
  - `AdminUserRepository` 作为 `Gateway` 实现，内部直接写 SQL 并返回 DTO
- 现有“domain”目录更像按业务模块整理代码，而非完整 DDD 分层

## Constraints

- 需基于现有代码库演进，而不是默认重写
- 当前仓库存在业务端 API 与管理端 API 双后端上下文
- 不能假设用户接受大范围破坏式重构
- 尚未发现现成的后端 DDD 设计文档或明确架构约束说明

## Unknowns / Open Questions

- 这次重构的核心业务动因是什么：可维护性、扩展性、团队协作、测试性，还是为某个即将到来的功能做准备
- DDD 落地范围是先覆盖一个核心域，还是对整个后端统一重构
- 是否接受渐进式迁移与新老结构并存
- 哪些模块必须优先纳入 DDD：业务端 API、管理端 API、还是公共模块
- 是否要求引入 CQRS、领域事件、聚合根、值对象、领域服务等“严格 DDD”构件
- 成功标准是什么：代码结构、测试覆盖、可扩展性、开发规范、还是某个示范模块上线

## Decision-Boundary Unknowns

- OMX 是否可以自行决定首个试点域
- OMX 是否可以自行设计新的分层包结构
- OMX 是否可以在不新增依赖的前提下重组模块与包
- OMX 是否可以允许短期内新旧架构并存
- OMX 是否可以将 admin-api 与 api 采用不同深度的 DDD 落地策略

## Likely Codebase Touchpoints

- `server/api/src/main/java/com/trailnote/api/domain/route/**`
- `server/api/src/main/java/com/trailnote/api/domain/creator/**`
- `server/api/src/main/java/com/trailnote/api/domain/interaction/**`
- `server/api/src/main/java/com/trailnote/api/domain/social/**`
- `server/admin-api/src/main/java/com/trailnote/admin/domain/**`
- `server/common/src/main/java/com/trailnote/common/**`
- `server/sql/init.sql`
- `server/pom.xml`
