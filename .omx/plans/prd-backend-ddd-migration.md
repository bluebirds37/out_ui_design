# PRD: TrailNote Backend One-shot DDD Migration

## Metadata

- Source spec: `.omx/specs/deep-interview-backend-ddd-refactor.md`
- Scope: `server/common`, `server/api`, `server/admin-api`
- Planning mode: `ralplan` consensus
- Risk level: high

## Problem

TrailNote backend code is organized by business-domain directories, but most modules still behave as `controller -> service -> mapper/JdbcTemplate` flows. This makes business logic, orchestration, persistence details, and DTO assembly accumulate in the same classes, which raises long-term maintenance and extension cost as features grow.

The user wants a one-shot migration of the entire backend into a DDD-oriented structure before further feature work continues.

## Goal

Restructure the whole backend into explicit DDD layers so future work lands in stable boundaries:

- interface/adapters for HTTP entry and externalized DTOs
- application layer for use-case orchestration
- domain layer for aggregates, domain services, repositories, policies, and business invariants
- infrastructure layer for MyBatis-Plus and JDBC persistence implementations

## Non-goals

- Do not change API contracts
- Do not change database schema unless separately approved later
- Do not add middleware or queues
- Do not split services/microservices
- Do not introduce event bus, CQRS, or event sourcing
- Do not add new dependencies without explicit request

## RALPLAN-DR Summary

### Principles

1. Preserve boundary clarity over short-term convenience.
2. Prefer evolutionary refactor on current modules over rewrite or platform change.
3. Keep contracts stable even while internal architecture changes.
4. Model domain behavior where business rules exist; do not force heavy DDD ceremony into thin CRUD paths.
5. Make the resulting structure the default landing zone for all future backend work.

### Top Decision Drivers

1. Full-backend one-shot migration is a user requirement.
2. `api` and `admin-api` use different persistence styles, so the architecture must allow asymmetric infrastructure depth.
3. The plan must improve maintainability without dragging in heavyweight DDD infrastructure.

### Viable Options

#### Option A: Strict full DDD module split per bounded context

- Shape:
  - each domain gets `interfaces`, `application`, `domain`, `infrastructure`
  - cross-cutting abstractions move into `common`
- Pros:
  - strongest boundary clarity
  - gives a clean, teachable target architecture
  - maximizes consistency for future feature work
- Cons:
  - largest refactor surface
  - high rename/move churn
  - risk of over-modeling thin admin CRUD flows

#### Option B: Hybrid layered DDD by backend module

- Shape:
  - keep `api` and `admin-api` as top-level runtime modules
  - within each domain, introduce DDD layers only where they materially map to business/use-case/infrastructure responsibilities
  - thin admin CRUD domains may use lightweight domain/application split
- Pros:
  - preserves current module boundaries
  - better fit for mixed MyBatis-Plus and JDBC realities
  - reduces accidental complexity in thin admin flows
- Cons:
  - less visually pure than strict DDD
  - requires discipline to prevent “lightweight” from sliding back into service scripts

### Chosen Option

Choose `Option B`.

Reason:

- It satisfies the user’s full-backend migration requirement without forcing identical implementation depth across `api` and `admin-api`.
- It preserves current deployment/runtime boundaries while still imposing clear DDD layering.
- It avoids adding ceremony where admin flows are mostly query/update oriented, while still removing controller/JdbcTemplate coupling from controllers.

### Alternatives Rejected

- Big-bang rewrite into new modules outside `server/api` and `server/admin-api`
  - rejected because the user wants evolution on the existing codebase, not a rewrite
- Pure package renaming without moving behavior into domain/application boundaries
  - rejected because it would improve appearance but not maintainability

## Target Architecture

## 1. `server/common`

Purpose:

- host cross-cutting primitives that are not tied to one domain model

Contents:

- shared response/error abstractions already used by both runtimes
- base architectural primitives only when truly reusable:
  - marker interfaces for use cases or repositories if needed
  - shared exception types
  - shared pagination/value helpers
- no business-domain logic for route, comment, user, or admin policies

Rule:

- `common` is not a dumping ground for things that merely happen to be reused twice

## 2. `server/api`

Recommended package topology:

- keep `api` as a runtime module
- keep each business domain as the first navigation boundary
- split layers inside each domain instead of forcing one giant horizontal tree

```text
com.trailnote.api
  route/
    interfaces/http/
    application/
    domain/
      model/
      repository/
      service/
      policy/
    infrastructure/
      persistence/mybatis/
  creator/
    interfaces/http/
    application/
    domain/
      model/
      repository/
      service/
      policy/
    infrastructure/
      persistence/mybatis/
  interaction/
    interfaces/http/
    application/
    domain/
    infrastructure/
      persistence/mybatis/
  social/
    interfaces/http/
    application/
    domain/
    infrastructure/
      persistence/mybatis/
  user/
    interfaces/http/
    application/
    domain/
    infrastructure/
      persistence/mybatis/
  search/
    interfaces/http/
    application/
    domain/
    infrastructure/
      persistence/mybatis/
  support/
```

Mapping guidance:

- controllers move to each domain's `interfaces/http`
- current `service` classes split into:
  - application use cases for orchestration
  - domain services/policies where business rules exist
- current `model` splits into:
  - domain models/value objects for business meaning
  - response/request DTOs in interfaces/application as appropriate
- current `persistence/entity` and `persistence/mapper` move under each domain's infrastructure layer
- repository interfaces live in the domain layer; MyBatis implementations/adapters live in infrastructure

Special attention domains:

- `creator` and `route` should carry the richest domain modeling
- `interaction` and `social` likely need lighter aggregates/policies
- `search` may remain mostly application + infrastructure query oriented

## 3. `server/admin-api`

Recommended layout:

```text
com.trailnote.admin
  auth/
    interfaces/http/
    application/
    domain/
    infrastructure/persistence/jdbc/
  user/
    interfaces/http/
    application/
    domain/
    infrastructure/persistence/jdbc/
  route/
    interfaces/http/
    application/
    domain/
    infrastructure/persistence/jdbc/
  comment/
    interfaces/http/
    application/
    domain/
    infrastructure/persistence/jdbc/
  dashboard/
    interfaces/http/
    application/
    domain/
    infrastructure/persistence/jdbc/
  support/
```

Mapping guidance:

- controllers should stop using `JdbcTemplate` directly
- `auth`, `user`, and `route` flows move behind application services/use cases
- gateway interfaces normalize into repository/query interfaces with consistent semantics
- thin reporting/dashboard queries can remain lightweight, but still live outside controllers

## Lightweight DDD Rule

Asymmetric depth is allowed, but not asymmetric layer semantics.

- every domain still gets a recognizable `interfaces -> application -> domain -> infrastructure` path
- thin CRUD or reporting domains may keep the domain layer slim:
  - domain models/value objects when meaningful
  - repository/query contracts
  - narrow policies/validation rules
- thin domains must not use “lightweight” as permission to keep controller-script or controller-JDBC logic
- richer domains such as `creator` and `route` should use aggregates, lifecycle methods, and explicit invariants where they materially exist

## Architecture Rules

1. Controllers/adapters must not talk directly to Mapper, JdbcTemplate, or SQL.
2. Application layer coordinates use cases and transactions.
3. Domain layer owns business terms, invariants, state transitions, and repository contracts.
4. Infrastructure owns entity mapping, SQL/MyBatis adapters, and technical integrations.
5. DTOs for transport must not become domain models by default.
6. Application code may depend on domain contracts, but not on infrastructure implementations.
7. New code should prefer moving logic into the new structure over preserving legacy service classes for convenience.

## Migration Strategy

Because the user requires a one-shot migration, execution should still be sequenced internally to control risk.

### Workstream 1: Architecture skeleton and conventions

- create target package structure in all backend modules
- define a domain-first vertical slice layout inside each runtime module
- define naming rules:
  - `*Controller` only in interfaces
  - `*UseCase` or `*AppService` in application
  - `*Repository` interfaces in domain
  - `*RepositoryImpl` or persistence adapters in infrastructure
- document transaction placement and DTO/domain boundaries
- document when a domain may stay lightweight and what minimum boundaries still apply

### Workstream 2: `server/api` migration

- migrate `creator` and `route` first because they contain the heaviest orchestration
- migrate `interaction`, `social`, `user`, `search` after conventions are proven
- remove direct mapper usage from controllers/services that become application/domain code

### Workstream 3: `server/admin-api` migration

- move controller-owned JDBC logic into application + infrastructure
- normalize auth/user/route/comment/dashboard structure
- remove controller-level data access and mixed permission/business logic

### Workstream 4: shared cleanup and guardrails

- converge shared exception and pagination primitives
- update tests to target new layers
- add a backend DDD guidance doc for future development

### Cutover Rule

- execution may sequence work internally by domain and runtime module
- merge/release completion is still one-shot:
  - do not treat the migration as complete until `common`, `api`, and `admin-api` all meet the target layer rules
  - do not declare success based on a single pilot domain

## Stories / Deliverables

1. Define and document the target DDD structure for all backend modules.
2. Migrate `server/api` domains to the new layered architecture.
3. Migrate `server/admin-api` domains to the new layered architecture.
4. Extract/normalize shared backend primitives without turning `common` into a catch-all.
5. Update verification coverage and development guidance so future work follows the new structure.

## Acceptance Criteria

1. Every backend domain is placed under a clear DDD-oriented layered structure.
2. No controller directly uses Mapper, `JdbcTemplate`, or SQL after migration.
3. Repository contracts sit on the domain side; MyBatis/JDBC implementations live in infrastructure.
4. Former mixed “service script” classes are either decomposed into application/domain responsibilities or explicitly retired.
5. `server/api` and `server/admin-api` both adopt consistent layer semantics, even if implementation depth differs.
6. A follow-up contributor can identify where to place a new feature without reinterpreting architecture from scratch.
7. Each domain has a single owning slice; a new feature should not require browsing a global layer tree to find its landing zone.

## Verification Strategy

Even though the user does not require behavior compatibility as a completion gate, the plan should still verify:

- architecture conformance:
  - package placement checks
  - no controller-to-mapper/JDBC shortcuts
  - no persistence implementation in domain layer
  - no domain-to-domain infrastructure leakage
- build correctness:
  - Maven compile and tests for all backend modules via `server/pom.xml`
- smoke behavior:
  - health/auth/core route flows where practical
- documentation:
  - backend DDD guidance recorded for future implementation

## Pre-mortem

### Scenario 1: Cosmetic DDD only

- Failure:
  - packages are renamed but old service-script behavior remains intact
- Prevention:
  - acceptance criteria target dependency direction and responsibility movement, not just folder names

### Scenario 2: Admin module remains controller-JDBC driven

- Failure:
  - `admin-api` keeps “temporary” controller data access because it looks simpler
- Prevention:
  - explicit rule that controller-level JDBC access is not allowed after migration

### Scenario 3: Structure-first goal causes unchecked runtime regressions

- Failure:
  - migration is declared complete even if core endpoints are broken
- Prevention:
  - keep runtime verification in the test spec and final execution lane, even if not a user hard gate

## Risks

1. The user’s structure-first completion criterion can incentivize under-testing.
2. One-shot migration across two backend runtimes can create large diff/merge and review risk.
3. Forcing uniform DDD depth across thin CRUD/reporting domains may create unnecessary ceremony.
4. Shared abstractions may be over-extracted into `common`, recreating a cross-cutting junk drawer.
5. MyBatis-Plus and JDBC differences may tempt inconsistent repository semantics unless naming is tightly enforced.
6. A horizontal package tree would make future feature ownership less obvious and encourage cross-domain edits for one use case.

## ADR

- Decision:
  - perform a one-shot full-backend migration to a hybrid layered DDD architecture
- Drivers:
  - user-mandated full migration
  - current controller/service/persistence coupling
  - mixed persistence stacks across api/admin-api
- Alternatives considered:
  - strict uniform DDD everywhere
  - cosmetic package reorg
  - gradual coexistence migration
- Why chosen:
  - best balance of consistency, feasibility, and fit to existing runtime boundaries
  - preserves domain-first navigation while still enforcing DDD layering
- Consequences:
  - large refactor surface, but clearer long-term architecture
  - requires strong naming and verification discipline
- Follow-ups:
  - execute via `ralph` or `team`
  - publish backend DDD conventions after migration

## Available Agent Types

- `architect`
- `executor`
- `verifier`
- `test-engineer`
- `code-reviewer`
- `critic`
- `explore`
- `writer`

## Staffing Guidance

### If executed via `ralph`

- Leader lane: `executor` with high reasoning
- Embedded review moments:
  - `architect` for package/layer boundary check
  - `verifier` for architecture and build evidence
  - `writer` for final backend DDD guidance doc

### If executed via `team`

- Lane 1: `architect` or `executor`
  - owns canonical package/layer skeleton and conventions
- Lane 2: `executor`
  - owns `server/api` migration
- Lane 3: `executor`
  - owns `server/admin-api` migration
- Lane 4: `test-engineer` or `verifier`
  - owns architecture checks, Maven verification, and smoke checks
- Lane 5: `writer`
  - owns migration notes and future DDD development guide

## Reasoning Guidance By Lane

- architecture lane: `high`
- implementation lanes: `high`
- verification lane: `medium` to `high`
- documentation lane: `medium`

## Launch Hints

- `ralph .omx/plans/prd-backend-ddd-migration.md`
- `$team .omx/plans/prd-backend-ddd-migration.md`

## Team Verification Path

1. Verify dependency direction and forbidden shortcuts.
2. Run backend build/tests.
3. Run targeted smoke checks for key API/admin flows.
4. Confirm documentation and final architecture map exist.
