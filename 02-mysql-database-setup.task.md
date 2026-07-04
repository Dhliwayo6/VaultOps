# Task 02: MySQL Database Setup and Configuration

## Context

The backend is Spring Boot and must run against MySQL in every environment (local, test, production). Confirm the current database configuration, then bring it fully in line with a production grade MySQL setup, including indexes implied by the query patterns flagged in `ANALYSIS.md`.

## Tasks

### Configuration
- [ ] Inspect `application.properties` (main) and confirm the datasource driver, URL, username, and password are MySQL, not H2 or another embedded database. Update if needed.
- [ ] Move all credentials (username, password, connection URL) out of `application.properties` and into environment variables, with `application.properties` referencing them via `${VAR_NAME}` placeholders.
- [ ] Create a separate `application-test.properties` (or confirm the existing `src/test/resources/application.properties`) that points to a dedicated test database or an in-memory MySQL-compatible mode, so tests never run against a shared dev/prod database.
- [ ] Configure a connection pool (HikariCP, which ships with Spring Boot by default) with explicit `maximum-pool-size`, `minimum-idle`, and `connection-timeout` values appropriate for the expected load, rather than relying on defaults silently.
- [ ] Add `docker-compose.yml` service for MySQL (if not already present) so local development can spin up a matching database with one command, including a healthcheck.

### Schema and migrations
- [ ] Introduce a schema migration tool (Flyway is the natural fit for Spring Boot). Add the dependency, configure the migration location, and generate an initial baseline migration from the current JPA entity state.
- [ ] Turn off `spring.jpa.hibernate.ddl-auto=update` (or whatever auto-DDL setting is currently active) in favor of migrations owning schema changes going forward. Auto-DDL is fine for the very first baseline generation only.
- [ ] Write the initial migration scripts for all existing tables: `Asset`, `Maintenance`, `Migration`, `ImportLog`, `ImportError`, and any join tables.

### Indexing
- [ ] Add indexes on `Asset.conditionStatus`, `Asset.usageStatus`, and `Asset.assignedTo` (or the `Assignment` enum column), since stats and filtering queries filter on these columns per the analysis.
- [ ] Add an index on `Asset.createdAt` to support the corrected "top 4 most recent" queries from Task 03.
- [ ] Add appropriate foreign key indexes for any relationships (`Maintenance` to `Asset`, `Migration` to `Asset`, `ImportError` to `ImportLog`) if not already indexed by the foreign key constraint itself.
- [ ] Document each added index in the migration file with a short comment explaining which query pattern it supports.

### Validation
- [ ] Confirm all repository tests and controller tests still pass against the new MySQL-backed configuration.
- [ ] Confirm the app starts cleanly against a freshly created, empty MySQL database using only the Flyway migrations (no manual schema setup).

## Acceptance Criteria

- The application connects to MySQL in every environment through environment-variable-driven configuration.
- Schema is fully owned by versioned Flyway migrations, not auto-DDL.
- All columns used in filtering, sorting, or joining have appropriate indexes.
- A fresh clone of the repository can run `docker-compose up` and get a working MySQL instance with the correct schema applied automatically.

## Constraints

- Do not change entity field names or types in this task unless required to match a chosen MySQL column type. Any such change must be reflected in both the entity and the migration script.
- Do not add caching in this task. That is covered separately in Task 05.
