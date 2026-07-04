# Task 09: Frontend Performance

## Context

With the app functionally complete and responsive, this task focuses purely on load time, runtime smoothness, and measurable performance scores.

## Tasks

### Image optimization
- [ ] Audit every image asset used across `Hero`, `Features`, and any other visual sections for correct format (prefer modern formats such as WebP where supported, with a fallback) and appropriately sized source files rather than a single oversized image scaled down in CSS.
- [ ] Add explicit `width` and `height` (or an `aspect-ratio`) on every image to prevent layout shift while images load.

### Lazy loading
- [ ] Apply native `loading="lazy"` to below-the-fold images.
- [ ] Code-split heavy, non-critical routes or components (for example anything only needed once a user is deep in the `Portal`, if it is large) using dynamic `import()` with React's `lazy` and `Suspense`, so the initial bundle only contains what is needed for the first paint.

### Animation library usage
- [ ] If Framer Motion (or a similar animation library) is in use anywhere in the project, audit every usage for unnecessary re-renders or overly broad animated properties, and confirm animations are GPU-friendly (`transform`/`opacity`) rather than animating layout-triggering properties (`width`, `top`, `left`) where avoidable.
- [ ] If no animation library is currently in use and none is needed, confirm this task's animation-related items are not applicable and note that in the completion summary rather than introducing a new dependency solely to satisfy this checklist item.

### Render optimization
- [ ] Audit `Dashboard`, `Assets`, and `Sidebar` for unnecessary re-renders using React DevTools' profiler, looking specifically for inline object and function literals passed as props to expensive children, and for state living higher in the tree than it needs to.
- [ ] Apply `useMemo`/`useCallback` or component memoization only where profiling actually shows a measurable benefit, rather than applying it reflexively everywhere.
- [ ] If the assets list can grow large, add list virtualization (rendering only visible rows) rather than rendering every row in the DOM at once.

### Bundle size
- [ ] Run a production build and inspect the bundle with a visualizer (for example `rollup-plugin-visualizer`, since the project builds with Vite/Rollup) to identify the largest contributors.
- [ ] Remove unused dependencies, confirm tree-shaking is working as expected, and replace any unnecessarily large library with a lighter alternative if a clear win is available.

### Font loading
- [ ] Confirm web fonts use `font-display: swap` (or an equivalent strategy) so text remains visible during font load rather than invisible.
- [ ] Preload the critical font file(s) used above the fold, and confirm only the font weights actually used are loaded, not the entire family.

### Lighthouse and Core Web Vitals
- [ ] Run Lighthouse against the production build for `Home`, `SignIn`, and `Portal`/`Dashboard`, and record baseline scores before and after this task's changes.
- [ ] Specifically target Largest Contentful Paint, Cumulative Layout Shift, and Interaction to Next Paint, addressing the largest contributor to each metric identified by Lighthouse.
- [ ] Document the before and after scores in a short `PERFORMANCE.md` note so future regressions are easy to spot.

## Acceptance Criteria

- Lighthouse performance score improves measurably from the recorded baseline on all audited pages.
- No image causes visible layout shift while loading.
- The production bundle has no clearly unnecessary large dependency.
- `PERFORMANCE.md` exists with before and after metrics.

## Constraints

- Do not sacrifice the visual design established in Task 08 to gain performance. The goal is the same look, delivered faster and more efficiently.
