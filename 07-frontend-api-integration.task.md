# Task 07: Frontend API Integration

## Context

`ANALYSIS.md` found that `Dashboard`, `Assets`, and `Sidebar` all run on hardcoded, static data (`DUMMY_ASSETS`, static `stats` arrays), and that `Sidebar` is mounted with a broken `activeTab` prop and a `setActiveTab` function that throws. This task wires the real backend into every screen and fixes the sidebar bug.

## Tasks

### API client layer
- [ ] Create a single `src/api` folder with one small module per resource (`assetsApi.js`, `statsApi.js`, `maintenanceApi.js`, `migrationsApi.js`, `importApi.js`, `exportApi.js`, `authApi.js` from Task 06), each exporting plain functions (`getAssets`, `createAsset`, `updateAsset`, `deleteAsset`, `searchAssets`, and so on) built on the authenticated fetch wrapper from Task 06.
- [ ] Make sure every function returns a consistent shape (data or a thrown, catchable error) so components can handle loading, success, and error states uniformly.
- [ ] Align every enum value used in requests (`Usage`, `ConditionStatus`) exactly to `ENUMS.md` from Task 03. Remove the mismatched frontend values (`IN_STORAGE`, `IN_SERVICE`, `POOR`, extra `DAMAGED` under `Usage`) and replace them everywhere they appear in the UI, including any dropdowns, filters, and badge/label rendering.

### Dashboard
- [ ] Replace the static `stats` and `recentAssets` arrays in `DashboardTools` with real calls to the consolidated `DashboardStatsDTO` endpoint and a real recent-assets query.
- [ ] Add loading and error states to the dashboard so a slow or failed request is visibly handled, not silently blank.

### Assets
- [ ] Replace `DUMMY_ASSETS` in `AssetTools` with real paginated calls to the assets list endpoint, including working search and filter controls wired to the backend `search` capability.
- [ ] Wire `AddAssetButton` and any edit/delete UI to the real create, update, and delete endpoints, with optimistic or refetch-based UI updates and visible error handling on failure.

### Sidebar
- [ ] Fix `Portal` so it no longer mounts `Sidebar` with an empty string `activeTab` and a `setActiveTab` that throws. Either lift real state into `Portal` and pass working handlers, or, preferably, replace manual active-tab tracking with React Router's `NavLink`, which derives active styling from the current route automatically.
- [ ] Render the icons that are already imported in `SidebarTools` but currently unused in `Sidebar`'s markup.

### Cross-cutting
- [ ] Add a shared `Loading` and `ErrorState` (or similar) presentational component used consistently across Dashboard, Assets, and any other data-driven view, instead of each screen inventing its own loading/error handling.
- [ ] Confirm every screen that previously used mock data now reflects real backend state after a create, update, or delete action, without requiring a manual page refresh.

## Acceptance Criteria

- No component imports or references hardcoded dummy data arrays.
- Frontend enum values match the backend exactly, confirmed against `ENUMS.md`.
- The sidebar correctly highlights the active section and every link navigates and updates state correctly.
- Every data-driven screen has visible, distinct loading, error, and empty states.

## Constraints

- This task depends on Task 01 (plain JS frontend), Task 03 (enum reference and corrected top-4 endpoints), and Task 04 (consolidated backend endpoints, particularly the single dashboard stats endpoint).
- Do not restyle or redesign any screen in this task, integration only. Visual and responsive work is covered in Task 08, and performance and accessibility polish in Tasks 09 and 10.
