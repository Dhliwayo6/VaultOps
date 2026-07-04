# Task 08: Responsiveness

## Context

By this point the frontend is plain JavaScript, wired to the real backend, and functionally correct. This task makes every screen genuinely responsive across the full range of device sizes, rather than only looking correct at whatever viewport it was originally built for.

## Tasks

### Mobile optimization
- [ ] Audit every page (`Home`, `SignIn`, `SignUp`, `OtpActivation`, `Portal` and its children: `Dashboard`, `Assets`, `Sidebar`) at common mobile widths (around 360px to 480px).
- [ ] Convert the sidebar to a collapsible or off-canvas pattern on mobile rather than a fixed-width column that competes with content for space.
- [ ] Confirm tap targets (buttons, nav items, form controls) meet a minimum comfortable size on touch devices.
- [ ] Confirm no horizontal scrolling occurs on any page at mobile widths unless a table or grid intentionally scrolls within its own container.

### Tablet optimization
- [ ] Audit every page at common tablet widths (around 600px to 900px), including both portrait and landscape orientation.
- [ ] Adjust grid and card layouts (asset cards, dashboard stat cards) to use an appropriate column count at tablet widths, not simply the mobile single-column or desktop multi-column layout stretched or squeezed.

### Desktop optimization
- [ ] Audit every page at common desktop widths (around 1280px to 1600px) and confirm layout, spacing, and alignment look intentional rather than like a stretched mobile layout.

### Ultra-wide optimization
- [ ] Audit every page at ultra-wide widths (1920px and above, and at least one ultra-wide monitor resolution such as 2560px or 3440px).
- [ ] Apply a sensible max content width with centered layout, or an intentional wide layout (extra sidebar content, wider tables), so content does not stretch edge to edge and become hard to scan.

### Responsive typography
- [ ] Define a type scale using relative units (`rem`) and confirm font sizes step down appropriately at smaller breakpoints, especially for headings on `Home` and `Dashboard`.
- [ ] Confirm line length stays within a readable range (roughly 45 to 75 characters) for body text at every breakpoint, adjusting max-width on text containers if needed.

### Responsive spacing
- [ ] Define spacing using a consistent scale (a set of tokens or utility classes) and confirm padding and margin reduce sensibly at smaller breakpoints rather than staying fixed and cramped or oversized.
- [ ] Confirm consistent spacing rhythm between sections on every page at every breakpoint audited above.

### Responsive animations
- [ ] Confirm any existing transition or animation (hover states, page transitions, sidebar collapse/expand) performs smoothly at every breakpoint, and is either reduced or removed on very small screens if it causes layout jank on lower powered mobile devices.
- [ ] Respect the user's `prefers-reduced-motion` setting by disabling or substantially simplifying non-essential animations when it is set.

## Acceptance Criteria

- Every page has been manually verified at mobile, tablet, desktop, and ultra-wide widths with no broken layout, overflow, or unreadable text.
- Typography and spacing scale sensibly across breakpoints rather than using one fixed set of values everywhere.
- Animations respect `prefers-reduced-motion` and do not cause visible jank on mobile.

## Constraints

- This task is visual and layout work only. Do not change data fetching or business logic here.
- Use the CSS approach already established in the project (CSS modules, plain CSS, or Tailwind, whichever is present) rather than introducing a second styling system.
