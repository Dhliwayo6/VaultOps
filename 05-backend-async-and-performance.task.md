# Task 05: Async Processing, Streaming Exports, and Caching

## Context

`ANALYSIS.md` found that `AsyncConfig` and `CacheConfiguration` are defined but never actually used, that Excel export loads the full workbook into memory, and that import processing blocks the request thread. This task wires up the infrastructure that already exists in config and applies it where it matters.

## Tasks

### Streaming export
- [ ] Replace the in-memory `XSSFWorkbook` usage in `AssetExportService` with Apache POI's `SXSSFWorkbook` (streaming user model), writing rows in batches and flushing to the HTTP response's output stream rather than building a full `ByteArrayOutputStream` in memory.
- [ ] Confirm the export endpoint sets the correct response headers for streamed content (`Content-Disposition`, `Content-Type`) and that the client receives the file correctly for both small and large datasets.
- [ ] Add a test (or a manually documented verification step, if generating tens of thousands of rows in a test is impractical) that confirms memory usage does not scale linearly with export size in the way the old implementation did.

### Asynchronous import
- [ ] Mark the actual file-processing method in `AssetImportService` with `@Async`, using the executor defined in `AsyncConfig`.
- [ ] Change the import controller endpoint to accept the file, persist an `ImportLog` row with status `PENDING`, kick off the async processing, and return immediately with the `ImportLog` id so the client can poll for status.
- [ ] Update the async processing method to update the `ImportLog` status to `IN_PROGRESS`, then `COMPLETED` or `FAILED`, writing any row-level failures to `ImportError` as it currently does.
- [ ] Add a `GET` endpoint to fetch import status and errors by `ImportLog` id, backed by `JobNotFoundException` if the id does not exist (reviving that exception here if it was kept from Task 03).
- [ ] Add tests covering a successful async import, a partially failed import (some rows have `ImportError` entries), and a fetch of status for a nonexistent job id.

### Caching
- [ ] Apply `@Cacheable` to the read-heavy, rarely-changing stats queries in the new `AssetStatsService` (Task 04), with a short, explicit TTL appropriate for a dashboard (for example 60 seconds), using the cache manager defined in `CacheConfiguration`.
- [ ] Apply `@CacheEvict` on the relevant cache region whenever an asset is created, updated, or deleted, so stats do not go stale beyond the TTL window in any meaningful way.
- [ ] Confirm the cache configuration is appropriate for a single-instance deployment for now, and note in a comment that a distributed cache (such as Redis) would be needed if the app is horizontally scaled later.

## Acceptance Criteria

- Exporting a large asset list does not require holding the entire workbook in memory at once.
- Uploading an import file returns immediately with a trackable job id, and the frontend can poll for completion status and see row-level errors.
- Dashboard stats are cached with a sensible TTL and are correctly invalidated on writes.
- `AsyncConfig` and `CacheConfiguration` are both genuinely exercised by the running application, not dead configuration.

## Constraints

- Do not change the shape of the existing export file format (columns, sheet layout) unless required by the streaming implementation.
- Do not introduce a message queue or external job runner in this task. `@Async` with the existing thread pool executor is sufficient at this stage.
