# Task 04: Backend Architectural Consolidation

## Context

`ANALYSIS.md` found a "one class per operation" pattern producing 8 classes for asset CRUD and 10 classes for stats, plus a duplicated DTO pair (`AssetDTO` and `AssetDTO2`) and dead Google Sheets code. This task consolidates the service layer into a clean, conventional structure without changing any external API contract, unless a contract change is explicitly called for below.

## Tasks

### Consolidate asset CRUD services
- [ ] Create a single `AssetService` in `com.vaultops.services` with methods `create`, `getById`, `getAll` (paginated), `update`, `delete`, `search`, `getTopFourInUse`, `getTopFourInRepairs`.
- [ ] Migrate the logic from `CreateAssetService`, `GetAssetService`, `GetAssetsService`, `UpdateAssetService`, `DeleteAssetService`, `SearchAssetService`, `GetTopFourAssetsInUseService`, `GetTopFourAssetsInRepairsService` into the corresponding methods.
- [ ] Update `AssetController` to depend on the single `AssetService` instead of eight separate service beans.
- [ ] Delete the eight old service classes once `AssetController` and all tests are passing against `AssetService`.
- [ ] Migrate and update the existing test files (`CreateAssetServiceTests`, `DeleteAssetServiceTest`, `GetAssetServiceTests`, `GetAssetsServiceTests`, `UpdateAssetServiceTests`) into a single `AssetServiceTests` class, preserving every existing test case.

### Consolidate stats services
- [ ] Create a single `AssetStatsService` with one method per distinct piece of information the dashboard needs, backed by repository-level aggregate queries (counts and averages pushed to MySQL, not computed in Java over a fully loaded list).
- [ ] Introduce a `DashboardStatsDTO` that bundles total assets, counts by usage status, counts by condition status, and average days in repair into a single response object.
- [ ] Update `StatsController` to expose one endpoint that returns `DashboardStatsDTO` in a single call, replacing the current pattern of one endpoint per stat if that is how it is currently structured. Confirm with the frontend task (Task 07) whether any existing individual endpoints must be preserved for backward compatibility, and if not, remove them.
- [ ] Delete the ten old stats service classes once `AssetStatsService` is fully in place and tested.

### Apply the same pattern to maintenance and migration
- [ ] Consolidate `services/maintenance` (`CreateMaintenanceService`, `DeleteMaintenanceService`, `GetMaintenanceService`, `GetMaintenancesService`, `UpdateMaintenanceService`) into a single `MaintenanceService`.
- [ ] Consolidate `services/migration` (`CreateMigrationService`, `DeleteMigrationService`, `GetMigrationService`, `GetMigrationsService`, `UpdateMigrationService`) into a single `MigrationService`.
- [ ] Update `MaintenanceController` and `MigrationsController` accordingly.

### DTO cleanup
- [ ] Compare every field used from `AssetDTO` and `AssetDTO2` across the codebase and decide the single DTO shape needed. Prefer one full `AssetDTO` (as a Java record) plus, if a genuinely lighter payload is needed for list views, a clearly and differently named `AssetSummaryDTO` rather than a numbered duplicate.
- [ ] Update `AssetMapperService` to be the single source of truth for all entity-to-DTO and DTO-to-entity conversion. No controller or service should construct a DTO manually outside of this mapper.
- [ ] Delete `AssetDTO2` once every usage has been migrated to the new naming.

### Remove dead code
- [ ] Delete the fully commented-out `GoogleSheetsConfig`, `GoogleSheetsImportService`, and `GoogleSheetsExportService`, along with their associated DTOs (`GoogleSheetsExportRequest`, `GoogleSheetsExportResponse`, `GoogleSheetsImportRequest`) and the Google API dependencies in `pom.xml`, unless a stakeholder confirms this feature is coming back soon. If it is coming back, move the commented code to a `docs/future-features.md` note instead of leaving it in source, and remove it from the compiled codebase either way.
- [ ] Confirm `NoAssetsMessageException` and `JobNotFoundException` were resolved per Task 03 and remove any now-orphaned imports.

## Acceptance Criteria

- The `services` package contains one service class per domain concept (`AssetService`, `AssetStatsService`, `MaintenanceService`, `MigrationService`, plus supporting services like `AssetImportService`, `AssetExportService`, `AssetValidationService`, `AssetMapperService`, `FileParserService`, `TemplateService`), not one class per operation.
- There is exactly one asset DTO for full detail and, if needed, one clearly and distinctly named summary DTO. No `AssetDTO2` remains.
- No commented-out or otherwise dead code remains in the backend source tree.
- Every existing test case still exists (migrated into the new consolidated test classes) and passes.
- All existing API endpoint contracts continue to work, or any intentional contract change is documented and reflected in the frontend integration task.

## Constraints

- Do not change the database schema in this task. Schema is owned by Task 02.
- Do not change validation rules or business logic behavior while consolidating, this is a structural refactor, not a logic change. Any bug found during consolidation should be flagged separately, not silently fixed inline, so it can be reviewed on its own.
