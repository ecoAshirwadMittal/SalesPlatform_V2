# Phase 1 Login — Parity Report vs QA Reference

**Compared:** 2026-04-23
**Reference:** `docs/qa-reference/qa-01-login.png` (Mendix QA, 1280×720 full-page)
**Our render:** `frontend/tests/e2e/parity-baselines/after-phase1-login.png` (local dev after Phase 1, same viewport)

## Matches (no action needed)

- Teal gradient container background — shade and top→bottom progression matches
- Cream card (#EFEBE4) with rounded corners and subtle drop-shadow
- ecoATM wordmark logo inside card (teal color, clearly readable)
- Header text: "Premium Wholesale & Weekly Auctions" — two lines, dark teal, matches
- Hero photo on left side (gloved hand holding a phone) — identical asset
- Email + Password input fields — cream input background, subtle border, matches
- Password eye-toggle icon (SVG replacement matches QA's image-swap idiom visually)
- "Remember me?" label with question mark — matches (verified in `LoginForm.tsx:199`)
- "Forgot Password?" teal link — matches
- "Login" pill button — dark teal/navy fill, white/cream text, pill radius
- "Employee Login" teal text link — matches
- Divider below Employee Login — matches
- "Interested but don't have an account?" subhead — matches
- "Contact Us" pill button — matches Login styling

## Minor proportional deltas (acceptable for Phase 1 — queue for final polish pass in Phase 13)

| Area | QA | Ours | Severity |
|---|---|---|---|
| Vertical padding above logo inside card | ~60px (generous breathing room) | ~40px (tighter) | Low |
| Login / Contact Us button width | Wider (fills ~80% of card form area) | Fixed 250px | Low-Medium |
| Logo size inside card | Appears ~200px wide | Currently ~180px wide | Low |
| Overall card height | Taller (~680–720px) | Shorter (~620px) | Low |

None of these rise to "doesn't match QA" — they're spacing polish that's best addressed in the Phase 13 pixel-QA pass once all surfaces are in a comparable state.

## Tokens + behavior verified

- All CSS token consumption — no hardcoded hex values except deferred `.error`/`.neutral` alert colors
- Brandon Grotesque loading confirmed (Phase 0 infrastructure)
- Password toggle is keyboard-accessible (`aria-pressed`, no `tabIndex={-1}`)
- Forgot Password link targets `/forgot-password` (Phase 15 will add the route stub)
- Contact Us is a `type="button"` no-op with `/* Contact URL TBD */` comment

## Verdict

**Phase 1 login parity is acceptable.** Proceeding to Phase 2. Final pixel polish will be revisited in Phase 13 against all parity baselines once the full flow is clone-complete.

## What I did NOT compare

- Hover/focus/active states (require interactive side-by-side; defer to Phase 13)
- Mobile breakpoints (Phase 13 responsive pass)
- Loading-state + error-state renders (QA screenshots did not capture these)
