# Partial Credit — Figma Parity Fix Plan

**Drafted:** 2026-05-12
**Source triage:** `docs/tasks/figma-parity/SUMMARY.md` (193 findings across 12 surfaces)
**Canonical token source:** `docs/frontend/mendix-partial-credit-stylesheet.css`
**Parent context:** Phase 1, Sprints 1–4 of the partial-credit feature; functionally complete, design-parity drift.

This plan groups 193 findings into 8 PR-sized fix groups, sequenced across 3 dispatch batches, and reconciles every "wrong token" finding against the Mendix `pg-partial-credit` stylesheet.

---

## Section 1 — Reconciliation table (parity reports vs. Mendix stylesheet)

The Mendix stylesheet is **the** source of truth for tokens. Where parity reports cite a different value, this table forces the correction before any group runs.

| # | Finding source (severity) | Parity-report claim | Mendix stylesheet truth | Action |
|---|---|---|---|---|
| R1 | SUMMARY + every report (HIGH × 6) | Brand green is `#14AC36` | `--eco-green: #1F8B3D` | All "green" fixes target `#1F8B3D` (NOT `#14AC36`). |
| R2 | Email-templates, on-behalf, admin landing | Green-hover unspecified or `#0F9C2E` | `--eco-green-hover: #176E30` | Use `#176E30` on hover everywhere. |
| R3 | SUMMARY (HIGH × 6) | Font is "Founders Grotesk" | Stack: `"Founders Grotesk", "Brandon Grotesque", "Helvetica Neue", Arial, sans-serif` | Use full fallback chain; Brandon Grotesque is the legitimate L2 fallback. |
| R4 | Wizard / detail reports | Text/body color `#1C1B1C` | `--eco-text: #1B1B1B` | Use `#1B1B1B`. Off by one hex digit. |
| R5 | Wizard reports | Muted text `#3C3C3C` / `#534F4C` | `--eco-text-muted: #3C3C3C` | `#3C3C3C` (matches code). Drop the `#534F4C` row. |
| R6 | Multiple | Border `#E6E5E4` / `#D0D0D0` | `--eco-border: #DDDDDD`; row-divider `#E8E8E8` | Use `#DDDDDD` for card/table outer border, `#E8E8E8` for row dividers. |
| R7 | Buyer detail | Reason header background `#F7F5F1` | Not in Mendix stylesheet (admin-only token from Figma) | Keep `#F7F5F1` for admin surfaces only; buyer surfaces use white. |
| R8 | Admin landing (HIGH) | Cell border `0.81px solid #A8A7A6` | Not in Mendix stylesheet; admin-only spec | Keep as admin-table-only. Buyer tables stay on `#DDDDDD`. |
| R9 | Multiple | Card radius `8px` | Implicit `8px` on datagrid in stylesheet | Confirmed `8px` everywhere. |
| R10 | Buyer detail (HIGH) | "Approved Credit panel highlight `fill_NH6QXG`" — hex unresolved | No analog in Mendix stylesheet | Flag to human review — DO NOT invent a hex. |
| R11 | Status config (HIGH) | Save button `#00969F` teal | Mendix primary CTA is `--eco-green: #1F8B3D` | Switch to `#1F8B3D`. |
| R12 | Status config (HIGH) | Default badge "bootstrap green" `#1F5A38 / #E6F4EC / #2D7A4E` | Use `--eco-green` family | Retint to `#1F8B3D` foreground, `#F1FAF3` background, `#1F8B3D` border. |
| R13 | Email-templates / on-behalf (HIGH) | `var(--color-brand-green, #407874)` — fallback is teal | `--color-brand-green` resolves to `#14AC36` in globals; correct value per Mendix is `#1F8B3D` | Fix BOTH: introduce `--color-pc-green` and drop teal fallbacks. |
| R14 | Wizard fonts | "Inherits Brandon Grotesque from body" | Mendix stack is Founders Grotesk → Brandon Grotesque → … | Introduce `--font-family-partial-credit` scoped to a `.pg-partial-credit` wrapper class. Do NOT mutate global `--font-family-primary` (auction-app font). |
| R15 | Wizard reports | Card padding `24px 32px` | Not in Mendix stylesheet (admin/auction CSS only) | Keep `24px 32px` per Figma. |
| R16 | Admin email-templates LOW | Hardcoded error palette `#c0392b / #fdecea` | No Mendix counterpart | Out of scope unless time permits. |
| R17 | Step indicator (HIGH) | "UTF-8 ✓ should be FA `check`" | Mendix stylesheet doesn't mention step indicator | Use inline SVG `check` glyph (avoid FA dep). |
| R18 | Admin review (HIGH) | "Reason card background `#F7F5F1` cream" | Admin-only; not in Mendix `pg-partial-credit` shared block | Keep `#F7F5F1` admin-only. |
| R19 | Buyer detail (CRITICAL) | "Multi-reason variant must use `Tabs - Partial Credit` + `Toggle - Partial Credit` components" | Out of scope of Mendix stylesheet | Component build needed; Mendix stylesheet does not block. |
| R20 | Surfaces 10/11/12 | No Figma frames | Mendix stylesheet doesn't constrain | These surfaces use Mendix-coherent tokens only (no pixel parity possible). |

**Net effect:** every reference to `#14AC36` in the parity reports is downgraded to `#1F8B3D`, every `#407874` fallback is replaced with `#1F8B3D`, and the font fix introduces a partial-credit-scoped variable rather than mutating the global token.

---

## Section 2 — Fix groups (PR-sized)

### Group 1 — Cross-cutting design tokens (foundation)

- **Scope:** `frontend/src/app/globals.css` + per-surface root-class application across every partial-credit page wrapper.
- **What it does:**
  1. Adds `--font-family-partial-credit` per the Mendix stack.
  2. Adds `--color-pc-green: #1F8B3D` and `--color-pc-green-hover: #176E30` to globals.
  3. Adds `--color-pc-border: #DDDDDD`, `--color-pc-row-divider: #E8E8E8`, `--color-pc-text: #1B1B1B`, `--color-pc-text-muted: #3C3C3C`, `--color-pc-bg-soft: #F7F7F7`.
  4. Adds a `.pg-partial-credit` root class on every partial-credit page wrapper so cascade is bounded (mirrors Mendix convention).
  5. Does NOT mutate `--color-brand-green` globally — that token is consumed by auction pages.
- **Findings covered:** All "wrong font" (HIGH × 6) and "wrong green fallback" (HIGH × 5) findings = ~14 findings.
- **Effort:** S (2–3 h).
- **Risk:** LOW — token additions only.
- **Dependencies:** Foundation. Must land before Groups 2–8.

### Group 2 — Admin landing column realignment + empty state

- **Scope:** `admin/auctions-data-center/partial-credit/page.tsx`, `admin.module.css`.
- **What it does:** 7-column Figma table (`Date Submitted | Buyer | Company | Order Number | Request Reason | Status | (eye)`); empty-state copy fix; neutral status pills; cell border `0.81px #A8A7A6`; drop teal fallback on Download.
- **Findings covered:** Surface 8 only = ~18 findings. Status config split into Group 8.
- **Effort:** M (½ day).
- **Risk:** Column-set change touches Buyer + Company DTO fields.
- **Dependencies:** Group 1.

### Group 3 — Admin review detail trio (HeaderStrip + bulk banner + Wrong table shape)

- **Scope:** `partial-credit/[id]/AdminReviewClient.tsx`, `_components/HeaderStrip.tsx`, Wrong-table refactor, `admin.module.css`.
- **What it does:** Buyer/Company split in HeaderStrip; remove global Accept-All bulk banner; restructure Wrong table to 10-column Figma layout (drop Recommendation column → move to dropdown default + tooltip); Missing table reorder; add `<h1>Request Details</h1>`; pluralization split; section cream background; Approve verb fix; replace native dropdown with custom; status-pill DTO color; internal status text.
- **Findings covered:** Surface 9 = 23 findings.
- **Effort:** L (1–1.5 days).
- **Risk:** HIGH — touches admin reviewer's primary workflow; custom dropdown a11y; DTO field rename.
- **Dependencies:** Group 1.

### Group 4 — Buyer detail reason tables + multi-reason Tabs/Toggle

- **Scope:** `[id]/_components/BuyerLineSection.tsx`, `BuyerHeaderStrip.tsx`, `BuyerSummaryPanels.tsx`, `page.tsx`, `detail.module.css`, NEW `MultiReasonTabs.tsx` + `ApprovedDeclinedToggle.tsx`.
- **What it does:** Fix Missing/Wrong/Encumbered column shapes; HeaderStrip dropped "Company" + added "Approved Date"; SummaryPanels 2-column (Qty/Total); build multi-reason tabs + toggle component; page heading `Credit Request Details` at 40px/500; pluralize section headings; server-driven status pill; "Add Photos" primary button.
- **Findings covered:** Surface 7 = 28 findings.
- **Effort:** L (1.5 days).
- **Risk:** Multi-reason variant is net-new component code. DTO additions must land server-side first.
- **Dependencies:** Group 1. Backend DTO fields.

### Group 5 — Buyer wizard reason-step layout (Missing + Wrong + Encumbered)

- **Scope:** `new/missing/MissingDevicesStep.tsx`, `new/wrong/...`, `new/encumbered/...`, `wizard.module.css`, `StepIndicator.tsx`.
- **What it does:** Build shared `BarcodeEntryCard` (textarea → OR divider → dropzone) on all 3 reason steps; add in-step parsed-review for Missing + Encumbered (Wrong has them but needs Photos column + sort icons + count badge + xmark delete); replace UTF-8 ✓ with inline SVG; single continuous step-indicator line; remove Cancel from step 1; card chrome (box-shadow not border, 8px radius, 24px 32px padding, 800px max-width); typography bumps; button-row gap 8px; warning/error banner split; inline validation.
- **Findings covered:** Surfaces 3+4+5 = 54 findings.
- **Effort:** L (2 days — largest).
- **Risk:** Shared CSS touches all reason steps + Summary. `Add Missing Devices` + `Add Photos` modals deferred (Phase 2).
- **Dependencies:** Group 1. Coordinate with Group 6 on shared `wizard.module.css`.

### Group 6 — Buyer wizard Summary step rebuild

- **Scope:** `new/summary/SummaryStep.tsx`, `summary/page.tsx`, `wizard.module.css`.
- **What it does:** Restore H1 `Submit a Credit Request`; add metadata card; replace `<ul>` with per-group tables; add Show/Hide Details toggle + count badge; per-group Edit button; bottom row `Edit | Cancel | Submit Request`; clean confirmation modal (no buttons); section card chrome (8px radius, box-shadow); drop damage prompt from Summary.
- **Findings covered:** Surface 6 = ~10 findings.
- **Effort:** M (1 day).
- **Risk:** Largest functional rewrite of a single component.
- **Dependencies:** Group 1. CSS contention with Group 5 — recommend same agent serializes 5 then 6.

### Group 7 — Buyer landing + Start (small polish PR)

- **Scope:** `wholesale/partial-credit/page.tsx`, `new/page.tsx`, `wizard.module.css`, `StepIndicator.tsx`.
- **What it does:** Drop `+ ` prefix from landing CTA; remove breadcrumb on landing; add eye-icon column → routes to detail; empty-state inside table not standalone card; neutral status pills; drop R-2 banner border; remove Start step Cancel button.
- **Findings covered:** Surfaces 1+2 = ~12 findings.
- **Effort:** S (3 h).
- **Risk:** LOW. Step-indicator changes shared with Group 5 — sequence after 5.
- **Dependencies:** Group 1. Sequence after Group 5.

### Group 8 — Surfaces without Figma frames (10/11/12) + design coherence pass

- **Scope:** `statuses/page.tsx + statusConfig.module.css`, `email-templates/{page.tsx, EmailTemplateEditor.tsx, emailTemplates.module.css}`, `OnBehalfModal.tsx + onBehalfModal.module.css`.
- **What it does (token-coherence only — no pixel parity):**
  1. All three: replace `var(--color-brand-green, #407874)` → `var(--color-pc-green, #1F8B3D)`. Drop teal fallback.
  2. All three: apply `--font-family-partial-credit`.
  3. **Status config:** heading 40px / line-height 1.1; drop heading underline; align padding `24px 32px 80px`; drop uppercase + letter-spacing on `<th>`; retint default badge + toast; Save button `#00969F` → `#1F8B3D`; replace Courier `systemStatusCell` with neutral chip.
  4. **Email-templates editor:** format `changedDate` via `toLocaleDateString()`; add collapsible "Available variables" block above textarea; update preview-hint copy.
  5. **On-behalf modal:** add Back button on USER step; bump heading to 24px; widen modal or flex-wrap; drop pill backgrounds on step indicator; verify post-create banner exists on `/new?draftId=X` (escalate to CRITICAL if missing); rename "Buyer user" → "Buyer contact".
- **Findings covered:** Surfaces 10+11+12 = ~41 findings.
- **Effort:** M (¾ day).
- **Risk:** LOW.
- **Dependencies:** Group 1.

---

## Section 3 — Subagent dispatch plan

| Group | Agent | Validation step |
|---|---|---|
| 1 | `general-purpose` | `grep -rn "pg-partial-credit"` shows the class on every partial-credit page root; new tokens resolve in dev tools. |
| 2 | `general-purpose` | Admin vitest passes; column order grep; empty-state copy literal grep. |
| 3 | `general-purpose` | Sprint-3 admin review tests pass; no `bulkBanner` remains; HeaderStrip Buyer + Company resolve to different values. |
| 4 | `general-purpose` | Sprint-4 buyer detail tests pass; new tab + toggle components have unit tests; multi-reason fixture verifies tabs render. |
| 5 | `general-purpose` | All wizard reason-step tests pass; `BarcodeEntryCard` has unit tests; no Cancel on Step 1. |
| 6 | `general-purpose` | Summary tests pass; H1 grep; metadata card renders 3 rows. |
| 7 | `code-simplifier` | Landing + Start tests pass; no `+ ` prefix; eye column renders. |
| 8 | `general-purpose` | Three surfaces compile + pass; zero `#407874` / `#00969F` literals remain in those module CSS files. |

(Each group's full prompt outline is in the architect dispatch — same structure as Sprint 4 chunk subagent prompts.)

---

## Section 4 — Sequencing

```
Batch A (serial — must land first):
  Group 1 (foundation tokens)

Batch B (parallel — 4 agents):
  Group 2 (admin landing)
  Group 3 (admin review)
  Group 4 (buyer detail)
  Group 8 (surfaces 10/11/12)

Batch C (serialized wizard cluster + small polish):
  Group 5 → Group 6 → Group 7  (same agent, three sequential commits)
```

**Total batches:** 3.
**Parallelism:** Batch B = 4 concurrent agents.
**Estimated wall-clock with 4-agent parallelism:** ~3 days.

---

## Section 5 — Out-of-scope / open questions

1. **Surfaces 10/11/12 — no Figma frames.** Group 8 ships token coherence only.
2. **Mendix green `#1F8B3D` vs parity-report `#14AC36`.** Mendix wins per the file the user opened.
3. **`--font-family-primary` scope.** Plan introduces `--font-family-partial-credit` (not global mutation).
4. **Backend DTO extensions** (`buyerName`, `boxNumber`, `receivedImei`, `colorHex`, `internalStatusText`, `approvedDate`). Group 3 + 4 + 2 depend on these. Subagents surface dependencies rather than fabricate.
5. **Surface 7 Approved Credit panel highlight** — hex unresolved. Ship without highlight; open follow-up ticket.
6. **Surface 7 Download CTA** — no backend export endpoint. Stub as separate ticket.
7. **Surface 12 post-create banner** — verify presence; escalate to CRITICAL if missing.
8. **Admin Wrong-table Recommendation column** — Figma forbids; Group 3 drops it (moves to tooltip).
9. **`Add Missing Devices` / `Add Photos` modals** — deferred Phase 2. TODO markers only.
10. **Status config save-on-blur vs explicit Save** — Group 8 keeps explicit Save; flag plan-divergence.

---

**Executive summary:** 8 fix groups across 3 batches (1 alone, 4 parallel, 3 sequenced) = ~7–8 engineer-days, collapsed to ~3 wall-clock days with 4-way parallelism. Largest risk: backend DTO field availability for Groups 2/3/4 — subagents flag missing fields rather than fabricate. Adopted Mendix-canonical green `#1F8B3D` (not `#14AC36`).
