# QA Reference Screenshots

> **Purpose: design references, not automated test fixtures.**
>
> These PNGs are the **manual visual reference** for anyone porting a
> wholesale-buyer surface — they show what the Mendix original looked
> like at capture time. They are NOT consumed by Playwright's
> `toHaveScreenshot()` anymore. Pixel-compare now runs against local
> Linux baselines under `frontend/tests/e2e/__screenshots__/`. See ADR
> "2026-04-25 — Pixel-compare strategy: local baselines + semantic
> assertions" in `docs/architecture/decisions.md` and the implementation
> plan in `docs/TODO/pixel-compare-strategy-plan.md`.
>
> Use these references when:
> - Porting a new surface and you need to see what the QA original looked like
> - Reviewing a styling change to confirm visual intent matches the QA reference
> - Capturing additional surfaces from QA that aren't represented yet
>
> Do NOT use these as Playwright snapshot baselines — the data + OS render
> deltas make that strategy unreliable.

Ground-truth reference captures from the Mendix QA environment
(`https://buy-qa.ecoatmdirect.com`), used as the pixel-parity target for the
wholesale buyer portal port.

**Captured:** 2026-04-22 via Playwright MCP.
**Test account:** `akshay+4@learnfastthinkslow.com` (wholesale bidder; holds
`AD` + `DDWS` buyer codes under CHS Technology (HK) Ltd).
**Viewport:** 1280×720 full-page screenshots (browser_take_screenshot,
`fullPage: true`).

## Filename → Phase mapping

| File | Surface | Used by phase |
|---|---|---|
| `qa-01-login.png` | Login page at `/p/login/web` | Phase 1 |
| `qa-02-buyer-code-picker.png` | Post-login picker ("Weekly Wholesale Auction" section) | Phase 2 |
| `qa-03-bidder-dashboard-ad.png` | Bidder dashboard (AD code; one submitted $2.50 bid visible) | Phases 4–7 |
| `qa-04-carryover-empty-modal.png` | Carryover modal — empty state | Phase 9 |
| `qa-05-import-bids-modal.png` | "Import Your Bids" modal | Phase 10 |
| `qa-06-avatar-popover.png` | User avatar popover (Submit Feedback + Logout) | Phase 3 / 4 |
| `qa-07-sidebar-collapsed.png` | Bidder shell with sidebar collapsed (Model col visible) | Phase 4 |
| `qa-08-submit-success-modal.png` | "Your Bids have been Submitted!" modal (post-submit success) | Phase 8 |
| `qa-09-bidder-dashboard-ddws.png` | Bidder dashboard (DDWS variant, no bids entered) | Phase 4 / parity cross-check |
| `qa-10-submit-no-bids-modal.png` | "No Bids to Submit" modal (empty-state guard) | Phase 8 |

## Not captured here (deferred)

- DOWNLOAD mode (R1 closed, R2 not yet open) — needs a live auction in that
  state. Schedule with Phase 11 walkthrough.
- ALL_ROUNDS_DONE mode — same reason.
- ERROR_AUCTION_NOT_FOUND — needs an account with no active auction.
- Active Carryover state (non-empty) — needs prior-week bids. Defer.
- Round 2 / Round 3 variants of the bidder dashboard — R1 was live at
  capture time.

## Conventions

- **Filenames are stable.** Do NOT rename; the parity plan
  (`docs/tasks/wholesale-buyer-parity-plan.md`) references these paths.
- **One screenshot per surface.** If a surface materially changes on QA, add
  a dated variant (e.g. `qa-01-login-2026-05-15.png`) rather than overwriting.
- **Committed to git.** These files ground every phase's pixel-compare and
  must be available to reviewers without needing live QA access.

## Also of interest

- `docs/tasks/wholesale-buyer-qa-analysis.md` — the prose analysis doc that
  pairs with these captures. §5 lists the verified design tokens.
- `docs/tasks/wholesale-buyer-parity-plan.md` — the phased implementation
  plan that these reference against.
- `frontend/tests/e2e/parity-baselines/` — the **local** before/after
  snapshots captured during each phase's development. Compare those to the
  files here to audit pixel parity.
