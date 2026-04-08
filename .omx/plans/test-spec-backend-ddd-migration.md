# Test Spec: TrailNote Backend One-shot DDD Migration

## Metadata

- Source PRD: `.omx/plans/prd-backend-ddd-migration.md`
- Source requirements: `.omx/specs/deep-interview-backend-ddd-refactor.md`
- Test scope: backend architecture migration verification

## Verification Goals

1. Prove every backend module has moved to the target DDD layering.
2. Prove disallowed shortcuts are removed.
3. Prove the backend still builds after migration.
4. Prefer proving critical runtime paths still function, even though the user does not require this as a hard completion gate.

## Test Strategy

### 1. Architecture Conformance

Verify package and dependency direction rules:

- domains are organized as vertical slices with internal DDD layers
- controllers exist only in interface/adapter layer
- controllers do not reference MyBatis Mapper, `JdbcTemplate`, or SQL strings
- application layer does not import infrastructure implementations directly
- domain layer does not import infrastructure persistence implementations
- domain layer does not depend on another domain's infrastructure package
- repository interfaces live in domain
- MyBatis/JDBC implementations live in infrastructure
- old mixed `domain/*/service` script classes are either removed or reduced to explicit application/domain roles

Evidence methods:

- repo-wide symbol/import searches
- targeted code review checklist
- optional lightweight architecture assertions if introduced during execution

### 2. Build and Static Verification

Required checks:

- `mvn -f server/pom.xml test`
- compile errors resolved across all backend modules
- no lingering package/import breakage after moves

### 3. Behavioral Smoke Verification

Target smoke paths:

- public health endpoint
- auth/login-related admin path
- representative route query path
- representative creator draft path
- representative admin user or route management path

Expectation:

- these are strongly recommended checks, even though failure does not negate the user’s structure-first definition of completion

### 4. Documentation Verification

Required artifacts:

- backend DDD architecture guidance doc
- clear explanation of target layering and naming rules
- clear explanation of the domain-first vertical slice rule
- migration notes for where major old classes moved

## Test Matrix

| Area | Check | Type | Priority |
| --- | --- | --- | --- |
| `server/api` | DDD package/layer presence by domain | architecture | P0 |
| `server/api` | domain-first vertical slices preserved | architecture | P0 |
| `server/admin-api` | Controller no longer touches JDBC directly | architecture | P0 |
| `server/admin-api` | lightweight domains still honor all layer boundaries | architecture | P0 |
| `server/common` | Only cross-cutting primitives remain | architecture | P1 |
| Backend build | Maven tests/compile pass | build | P0 |
| Core API routes | targeted smoke checks | runtime | P1 |
| Admin routes | targeted smoke checks | runtime | P1 |
| Docs | architecture guidance exists | docs | P1 |

## Acceptance Checks

1. All backend domains can be mapped to interface/application/domain/infrastructure responsibilities.
2. All backend domains live in a domain-first slice layout rather than a global horizontal layer tree.
3. No controller references persistence adapters directly.
4. No domain package depends on MyBatis or JDBC implementation details.
5. Backend modules pass Maven verification.
6. Documentation exists for future feature placement.

## Expanded Test Plan

### Unit-level candidates

- domain policy/invariant tests introduced during migration
- application use case orchestration tests where behavior is non-trivial

### Integration-level candidates

- repository adapter tests for MyBatis/JDBC mappings where touched heavily
- Spring slice tests around application services and controllers if reorganized

### End-to-end / smoke candidates

- minimal HTTP smoke script covering public and admin core flows

### Observability / inspection

- grep-based architecture checks for forbidden dependencies
- grep-based checks for forbidden controller imports and domain-to-infrastructure imports
- build logs and failing package references collected as evidence

## Risks To Watch During Verification

1. Passing architecture checks while runtime wiring is broken.
2. Moved classes leaving stale imports or duplicate concepts.
3. Thin CRUD domains gaining accidental complexity from over-modeling.
4. `common` absorbing business logic under the guise of reuse.
5. Package moves succeeding mechanically while domain ownership becomes harder to navigate.

## Exit Criteria

- PRD acceptance criteria have evidence
- architecture shortcut checks are clean
- backend Maven verification completed
- documentation artifacts exist
- runtime smoke results are recorded, even if imperfect
