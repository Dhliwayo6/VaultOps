# Task 10: Accessibility

## Context

This task makes the application usable for people relying on the keyboard, assistive technology, or who need better contrast or reduced motion, across every page and every interactive component built in earlier tasks.

## Tasks

### Keyboard navigation
- [ ] Confirm every interactive element (nav links, buttons, form fields, the add/edit/delete asset controls, sidebar items) can be reached and operated using only the keyboard, in a logical tab order matching the visual layout.
- [ ] Confirm no keyboard trap exists anywhere, especially in any modal or dialog used for adding or editing an asset.
- [ ] Add a visible "skip to main content" link at the top of the page for keyboard users to bypass repeated navigation.

### ARIA labels
- [ ] Add appropriate `aria-label` or `aria-labelledby` to icon-only buttons (sidebar icons, add asset button, any close or delete icon) so their purpose is announced correctly.
- [ ] Add `aria-live` regions for asynchronous state changes that matter to screen reader users (import job status updates, form submission success or error messages).
- [ ] Confirm `aria-current="page"` (or the router's equivalent) is applied to the active sidebar link.

### Semantic HTML
- [ ] Audit every page for correct use of landmark elements (`header`, `nav`, `main`, `footer`, `aside` for the sidebar) instead of generic `div`s standing in for structural roles.
- [ ] Confirm heading levels (`h1` through `h6`) are used in a correct, non-skipping hierarchy on every page.
- [ ] Confirm lists of items (asset lists, stat cards) use `ul`/`ol`/`li` where semantically appropriate, or an ARIA grid/list role if a non-list element must be used for layout reasons.

### Focus states
- [ ] Confirm every focusable element has a visible focus indicator that meets sufficient contrast against its background, and is not removed by a global `outline: none` without a replacement.
- [ ] Confirm focus moves sensibly after key interactions, for example into a newly opened modal, and back to the triggering element when it closes.

### Accessible forms
- [ ] Confirm every form input on `SignIn`, `SignUp`, `OtpActivation`, and the add/edit asset form has a properly associated `label` (not only a placeholder).
- [ ] Confirm validation errors are associated with their field using `aria-describedby` and are announced to screen readers when they appear.
- [ ] Confirm required fields are marked both visually and with `aria-required` or the `required` attribute.

### Contrast testing
- [ ] Run every page's text and interactive elements (including on top of any background images or gradients in `Hero`) through a contrast checker and confirm compliance with WCAG AA at minimum for normal text and UI components.
- [ ] Fix any color pairing found to fail, adjusting the design token or specific override rather than only the single failing instance if the underlying palette choice is the root cause.

### Screen reader support
- [ ] Manually test the primary flows (sign up, OTP activation, sign in, view dashboard, add an asset, search assets) using a screen reader (VoiceOver, NVDA, or the equivalent available in the testing environment) and fix anything that reads confusingly, out of order, or not at all.
- [ ] Confirm decorative images have empty `alt=""` and informative images have descriptive `alt` text.

## Acceptance Criteria

- Every primary user flow can be completed using only the keyboard.
- A contrast check shows no WCAG AA failures on any audited page.
- A manual screen reader pass through the primary flows produces a coherent, correctly ordered experience with no unlabeled controls.
- All forms have properly associated labels and accessible error messaging.

## Constraints

- Do not change the visual design in ways that conflict with Task 08's responsive layout or Task 09's performance work; accessibility fixes should integrate with, not replace, that work. Where a genuine conflict exists (for example a low-contrast brand color), flag it rather than silently overriding the design.
