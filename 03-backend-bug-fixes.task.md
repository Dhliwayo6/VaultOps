# Task 03: Backend Runtime Bug Fixes and Correctness

## Context

`ANALYSIS.md` identified several concrete runtime bugs and correctness problems in the backend. This task fixes each one directly, with a regression test added alongside every fix.

## Tasks

### Temporal calculation bug
- [ ] In `GetNumberOfDayInRepairsService`, fix the `ChronoUnit.DAYS.between` call that currently mixes a `LocalDateTime` and a `LocalDate`. Convert `asset.getCreatedAt()` to a `LocalDate` before comparing, per the fix in the analysis.
- [ ] Add a unit test that exercises this service with a known `createdAt` value and asserts the correct day count, and confirms no `DateTimeException` is thrown.

### Top 4 assets query bug
- [ ] Add `findTop4ByUsageStatusOrderByCreatedAtDesc` (and the equivalent for the repairs variant) to `AssetRepository` using Spring Data JPA query derivation.
- [ ] Update `GetTopFourAssetsInUseService` and `GetTopFourAssetsInRepairsService` to call the new repository methods directly instead of loading the full result set, limiting to 4, and then sorting.
- [ ] Add a test with more than 4 matching assets that asserts the 4 most recently created ones are returned, in the correct order.

### Enum alignment between backend and frontend
- [ ] Treat the backend enums (`Usage`: `IN_USE`, `STORAGE`, `SERVICE`; `ConditionStatus`: `EXCELLENT`, `GOOD`, `FAIR`, `BAD`, `DAMAGED`) as the source of truth.
- [ ] Confirm every backend usage of these enums is consistent (controllers, DTOs, validation).
- [ ] Document the exact enum values and their meaning in a short `ENUMS.md` reference file, so the frontend task (Task 07) aligns to it exactly rather than guessing.
- [ ] Add a controller-level test that submits each valid enum value for both `Usage` and `ConditionStatus` and asserts a 200 response, plus a test that submits an invalid value and asserts a 400 with a clear error message from `GlobalExceptionHandler`.

### Cleanup of unused and dead exceptions
- [ ] Decide, for `NoAssetsMessageException` and `JobNotFoundException`, whether each has a real future use case (job status tracking is planned in Task 05, so `JobNotFoundException` may become live there) or should be deleted now. Delete if genuinely unused, keep with a comment noting its intended use if kept.

## Acceptance Criteria

- No `DateTimeException` is possible from the repairs-duration calculation, verified by a test.
- Top 4 endpoints return the true 4 most recent matching assets, verified by a test with more than 4 candidates.
- `ENUMS.md` exists and exactly matches the backend enum definitions.
- Every fix in this task has at least one corresponding automated test that would have failed before the fix and passes after it.

## Constraints

- Do not consolidate or restructure services in this task beyond what is required to fix the specific bugs listed. Broader consolidation is Task 04.
- Do not touch frontend code in this task.
