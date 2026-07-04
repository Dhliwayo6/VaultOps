# Task 11: Testing Coverage

## Context

`ANALYSIS.md` found that only `AssetController` and asset CRUD services had meaningful test coverage, with export, import, stats, maintenance, migration, and the repository layer entirely untested. By this point those services have been consolidated (Task 04), made async where relevant (Task 05), and secured (Task 06), so tests need to cover both the new structure and the previously untested areas.

## Tasks

### Backend unit and integration tests
- [ ] Add tests for `AssetStatsService`, covering every field on `DashboardStatsDTO` with known seeded data and an asserted expected result.
- [ ] Add tests for `MaintenanceService` and `MigrationService` covering create, read, update, and delete, including not-found error cases.
- [ ] Add tests for `AssetExportService` confirming the exported file contains the expected rows and column headers for a small, known dataset.
- [ ] Add tests for `AssetImportService` covering a fully successful import, a partially failed import with row-level errors recorded, and an import of a malformed file that fails cleanly with a clear error rather than an unhandled exception.
- [ ] Add tests for the import status polling endpoint from Task 05, covering pending, in progress, completed, and failed states, plus the not-found case.
- [ ] Add repository-layer tests under `com.vaultops.assets.repository` (currently empty) covering the custom query methods added in Task 03 (`findTop4ByUsageStatusOrderByCreatedAtDesc` and equivalent) and any other custom queries introduced during consolidation.
- [ ] Add tests confirming every protected endpoint rejects requests without a valid JWT, and accepts them with one, from Task 06.
- [ ] Add tests confirming `GlobalExceptionHandler` returns the correct HTTP status and error shape for every custom exception still in use.

### Frontend tests
- [ ] Set up a JavaScript-compatible test runner for the frontend (Vitest, matching the Vite build, using plain JavaScript test files since the project is no longer TypeScript).
- [ ] Add component tests for the sign in, sign up, and OTP activation forms covering successful submission, validation errors, and API failure states.
- [ ] Add a test confirming the sidebar correctly reflects the active route and that navigation updates it, covering the specific bug found in the analysis.
- [ ] Add tests for the Dashboard and Assets pages confirming loading, error, and populated states all render correctly given a mocked API response.

### Coverage reporting
- [ ] Confirm `jacoco` (already present in the backend) produces an up to date coverage report and set a reasonable minimum coverage threshold for the service layer going forward, failing the build if coverage drops below it.
- [ ] Add a coverage tool for the frontend test suite and report the baseline percentage in `TESTING.md`.

## Acceptance Criteria

- Every backend service and controller has meaningful test coverage, with no domain (assets, stats, maintenance, migration, import, export, auth) left entirely untested.
- The previously empty repository test package now contains real tests for custom queries.
- The frontend has an established test runner with coverage for the previously bug-prone areas (auth forms, sidebar navigation, dashboard and assets data states).
- `TESTING.md` documents current coverage numbers for both backend and frontend as a baseline for future work.

## Constraints

- This task depends on Tasks 03 through 07 being functionally complete, since it tests the consolidated, secured, and integrated version of the system rather than the original mocked or duplicated version.
- Do not treat high coverage percentage as the goal in isolation. Prioritize tests that would have caught the specific bugs found in `ANALYSIS.md` and tests around business-critical logic (stats calculations, import error handling, auth) over trivial getter/setter coverage.
