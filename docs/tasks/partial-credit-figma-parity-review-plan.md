# Partial Credit — Figma Parity Review Plan

**Drafted:** 2026-05-12
**Scope:** spec-vs-code parity check across every partial-credit surface
shipped in Phase 1 (Sprints 1-4).
**Method:** for each surface, pull the Figma node data, read the
corresponding local Next.js + CSS files, produce a structured findings
report covering typography, color, spacing, layout, copy, and behaviour.

## 1. Figma source

| Item | Value |
|---|---|
| Figma file key | `rYKB9vBqlJOFUuGN7GAgQS` |
| File name | `2026 Auction: Partial Credit` |
| Tool | `mcp__figma__get_figma_data(fileKey, nodeId)` for node trees; `mcp__figma__download_figma_images(...)` for screenshots if needed for visual checks |

Pre-existing parsed snapshots (use as background, not as a substitute
for fresh Figma reads):
- `docs/tasks/partial-credit-sprint2-design-notes.md` — node `173-600`
- `docs/tasks/partial-credit-sprint3-design-notes.md` — node `213-610`

## 2. Surface inventory

| # | Surface | Local route / files | Figma node | Sprint |
|---|---|---|---|---|
| 1 | Buyer landing | `/wholesale/partial-credit` → `frontend/src/app/(dashboard)/wholesale/partial-credit/page.tsx` + `wizard.module.css` | `173-600` (SECTION "Credit Requests Landing", lines 12246-13544 of the design dump) | 2 |
| 2 | Wizard step 1 — Start | `/wholesale/partial-credit/new` → `new/page.tsx` | `173-600` (SECTION "Start Request (R2 Certified)", lines 171-1369) | 2 |
| 3 | Wizard step 2 — Missing | `/wholesale/partial-credit/new/missing` → `new/missing/MissingDevicesStep.tsx` | `173-600` (SECTION "Missing Device" + per the design notes §3) | 2 |
| 4 | Wizard step 3 — Wrong | `/wholesale/partial-credit/new/wrong` → `new/wrong/...` | `173-600` (SECTION "Wrong Device") | 2 |
| 5 | Wizard step 4 — Encumbered | `/wholesale/partial-credit/new/encumbered` → `new/encumbered/...` | `173-600` (SECTION "Encumbered Device", lines 18979-21889) | 2 |
| 6 | Wizard step 5 — Summary | `/wholesale/partial-credit/new/summary` → `new/summary/...` | `173-600` (SECTION "Summary", lines 1370-11154) | 2 |
| 7 | **Buyer detail page** | `/wholesale/partial-credit/[id]` → `[id]/page.tsx` + `[id]/_components/*` + `[id]/detail.module.css` | `534-11895` (per Sprint 4 / SPKB-3669) | **4** |
| 8 | Admin landing | `/admin/auctions-data-center/partial-credit` → `admin/auctions-data-center/partial-credit/page.tsx` + `admin.module.css` | `213-610` (per Sprint 3 design notes §2) | 3 |
| 9 | Admin review detail | `/admin/auctions-data-center/partial-credit/[id]` → `[id]/AdminReviewClient.tsx` + `[id]/_components/*` | `213-610` (per Sprint 3 design notes §3) | 3 |
| 10 | Admin status config | `/admin/.../partial-credit/statuses` → `statuses/page.tsx` | `213-610` (SPKB-3664 sub-frame) | 3 |
| 11 | **Admin email templates editor** | `/admin/.../partial-credit/email-templates` → `email-templates/page.tsx` + `EmailTemplateEditor.tsx` | _No confirmed Figma frame_ — review against Sprint 4 plan §5.2 design intent | **4** |
| 12 | **On-behalf modal** | `/wholesale/partial-credit/OnBehalfModal.tsx` + landing trigger | _No confirmed Figma frame_ — review against Sprint 4 plan §5.3 design intent | **4** |

Bold rows are Sprint-4-introduced surfaces; non-bold rows are
Sprint-2/3 surfaces still expected to match their original Figma
frames.

## 3. Review dimensions

Each surface report must cover:

1. **Layout** — section ordering, column counts, header strip / table /
   panel positioning. Flag any element present in Figma but missing
   in code (or vice versa).
2. **Typography** — font family, size, weight, line height per the
   Figma `text` nodes vs. the relevant CSS module / globals.
3. **Color** — fills, borders, status pill colors. Cross-check against
   the design tokens declared in `frontend/src/app/globals.css` and
   the CSS module's local custom-property usage.
4. **Spacing** — paddings, gaps, margins. Allow ±2px for token-vs-pixel
   rounding; flag anything beyond that.
5. **Copy** — every visible string. Mismatches here are usually
   high-confidence catches (Figma's text is verbatim).
6. **Behaviour** — interactive states (hover, focus, active, disabled)
   and conditional renders (status-driven gating). Compare against the
   Figma frame variants where present.

Severity:
- **CRITICAL** — wrong copy, missing feature, broken interaction.
- **HIGH** — wrong color token / wrong typography weight / wrong layout.
- **MEDIUM** — spacing drift > 4px, missing hover state, minor copy
  capitalisation.
- **LOW** — cosmetic polish (icon size 1-2px off, color shade variant).

## 4. Per-surface report shape

Each subagent returns one markdown block per surface in this shape:

```markdown
## Surface: <name> (<route>)

**Figma node:** `<node-id>`
**Local files:** <path1>, <path2>, ...
**Variants reviewed:** <list of Figma variants>

### Findings

| Severity | Dimension | Finding | Figma value | Code value | Fix hint |
|---|---|---|---|---|---|
| CRITICAL | Copy | ... | ... | ... | ... |

### No-issues summary
- Layout matches Figma section ordering ✓
- ... (positives the reviewer confirmed, so we know what was checked)
```

The master triage doc (this plan doc) is **not** modified by subagents
— each subagent produces its own findings file under
`docs/tasks/figma-parity/<surface>.md` for traceability.

## 5. Subagent dispatch plan

Two batches of three agents each (avoids context contention). Every
agent is `general-purpose` with the Figma + Playwright MCP tools
available.

### Batch 1 — Buyer wizard surfaces (3 agents)

- **B1-A**: surfaces 1 (Landing) + 2 (Start) + 6 (Summary)
- **B1-B**: surfaces 3 (Missing) + 4 (Wrong) + 5 (Encumbered)
- **B1-C**: surface 7 (Buyer detail — Sprint 4)

### Batch 2 — Admin surfaces (3 agents)

- **B2-A**: surface 8 (Admin landing) + 10 (Status config)
- **B2-B**: surface 9 (Admin review detail)
- **B2-C**: surfaces 11 (Email templates) + 12 (On-behalf modal)

### Consolidation step

After both batches complete, the orchestrator (me) consolidates all
six findings files into a single master triage report at
`docs/tasks/figma-parity/SUMMARY.md` with:
- Total CRITICAL / HIGH / MEDIUM / LOW count by surface
- Top 10 fix recommendations (sorted by severity then by surface)
- Open questions (Figma frames that didn't have clear analogs in code)

## 6. Out of scope

- Pixel-perfect screenshot comparison — that's a separate "screenshots
  first" workflow the user explicitly opted out of in favour of
  spec-against-code review.
- Behavioural QA beyond what's visible in Figma (e.g. error states the
  Figma doesn't depict).
- Build / CI / deployment status — orthogonal to design parity.
- Surfaces without a confirmed Figma frame (rows 11 + 12) get a
  "spec sketch" review against the Sprint 4 plan §5.2 / §5.3 intent
  rather than a Figma diff.
