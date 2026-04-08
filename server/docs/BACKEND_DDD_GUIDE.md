# TrailNote Backend DDD Guide

## Goal

TrailNote backend now uses a domain-first DDD structure inside each runtime module.

- Runtime modules stay the same:
  - `server/api`
  - `server/admin-api`
  - `server/common`
- Each business slice is the first navigation boundary.
- Each slice then uses layered directories:
  - `interfaces`
  - `application`
  - `domain`
  - `infrastructure`

## Runtime Modules

### `server/api`

Business-facing API runtime.

Current slice examples:

- `com.trailnote.api.creator`
- `com.trailnote.api.route`
- `com.trailnote.api.interaction`
- `com.trailnote.api.search`
- `com.trailnote.api.social`
- `com.trailnote.api.user`
- `com.trailnote.api.auth`
- `com.trailnote.api.support`

### `server/admin-api`

Admin-facing API runtime.

Current slice examples:

- `com.trailnote.admin.auth`
- `com.trailnote.admin.user`
- `com.trailnote.admin.route`
- `com.trailnote.admin.comment`
- `com.trailnote.admin.dashboard`
- `com.trailnote.admin.support`

### `server/common`

Only cross-cutting primitives belong here:

- API response wrappers
- shared exceptions
- health/status primitives
- generic pagination/value helpers

Do not move business rules here just because two slices need them.

## Layer Responsibilities

### `interfaces`

HTTP adapters only.

- controllers
- request validation
- transport DTO mapping

Controllers must not:

- use `JdbcTemplate`
- use MyBatis Mapper interfaces
- embed SQL
- orchestrate multi-step business flows

### `application`

Use-case orchestration.

- coordinate transactions
- call repositories and domain logic
- assemble use-case outputs

Application services should describe an action or query from the product point of view.

### `domain`

Business meaning.

- domain models
- repository contracts
- policies/invariants where needed

Not every slice needs heavy aggregates, but every slice should still have a recognizable domain boundary.

### `infrastructure`

Technical implementation.

- MyBatis entities and mapper adapters
- JDBC repositories
- persistence-specific row/materialization logic

Infrastructure implements domain/application needs. It should not leak directly into controllers.

## Navigation Rule

Always enter the codebase through the business slice first, not a global layer tree.

Good:

- `com.trailnote.api.route.interfaces.http.RouteController`
- `com.trailnote.admin.route.application.AdminRouteManagementService`

Bad:

- one giant top-level `controller/`, `service/`, `repository/` tree shared by every domain

## Naming Rule

- `*Controller`: only in `interfaces/http`
- `*Service` or `*ApplicationService`: only in `application`
- `*Repository`: domain contract or infrastructure implementation depending on package
- MyBatis/JDBC-specific code: only in `infrastructure/persistence/...`

## Practical Rule For New Code

When adding a feature:

1. Pick the business slice first.
2. Add or update the controller under `interfaces/http`.
3. Put orchestration in `application`.
4. Put business concepts or contracts in `domain`.
5. Put SQL/MyBatis/JDBC work in `infrastructure`.

If a new feature would require editing a controller and a Mapper directly, stop and add the missing application or repository boundary first.

## Current Simplification Strategy

This migration intentionally prioritized structure and ownership boundaries first.

- API contracts stay unchanged.
- Database schema stays unchanged.
- No new middleware, queues, microservices, CQRS, or event sourcing were introduced.

Future iterations can deepen domain modeling inside rich slices like `creator`, `route`, and `auth` without undoing the current structure.
