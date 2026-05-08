# Datagrid filter icons — sprint plan (2026-05-08)

**Status:** Draft.

**Trigger:** Mendix's `datagrid-filters` icon font is the visual signature
of every QA grid (Reserve Bids, Inventory, PO, Auctions, Buyers, Users…).
QA renders comparator triggers using glyphs from this font (`>`, `≥`, `=`,
`≠`, `<`, `≤`, the empty/not-empty bars, the `Ab` text-comparator marker,
`Ab→` starts-with, `→Ab` ends-with, etc.). Local renders ASCII / unicode
fallbacks today. Closing this gap is what makes the admin app *feel* like
a continuation of QA across every page, not a redesign.

This is a **separate sprint** from the comparator dropdown work — the
dropdown ships first with stopgap glyphs, then this sprint replaces the
glyph rendering everywhere at once.

---

## 1. Scope

Glyphs needed across every grid filter:

| Op             | QA glyph (Mendix font)            | Stopgap (current) |
|---             |---                                |---                |
| Greater than          | `>` icon (filled chevron)  | `>` (ASCII)       |
| Greater than or equal | `≥` icon                   | `≥` (U+2265)      |
| Equal                 | `=` icon                   | `=` (ASCII)       |
| Not equal             | `≠` icon                   | `≠` (U+2260)      |
| Smaller than          | `<` icon                   | `<` (ASCII)       |
| Smaller than or equal | `≤` icon                   | `≤` (U+2264)      |
| Empty                 | `=̄` (equal with overbar)   | `∅` (U+2205) — not faithful |
| Not empty             | `≠̄` (not-equal with overbar) | `⊘` (U+2298) — not faithful |
| Contains              | `Ab` (with subtle marker) | `Ab` text         |
| Starts with           | `Ab` with leading caret    | `Ab→` text — not faithful |
| Ends with             | `Ab` with trailing caret   | `→Ab` text — not faithful |
| Calendar (date trigger) | calendar SVG             | native `<input type="date">` button |

The unicode fallbacks for the math ops (`≥`, `≤`, `≠`) are visually close
enough that a casual user wouldn't notice. The four where the gap shows
visibly are: **Empty / Not empty / Starts with / Ends with**. Plus the
calendar trigger (browser default vs custom).

---

## 2. Sourcing options

### Option A — License-clean reproduction (RECOMMENDED)

Build a custom SVG icon set that visually matches Mendix's glyphs. Each
op gets a 24×24 SVG drawn to mirror the Mendix shape (chevron weights,
overbar style, etc.) but authored in this codebase, MIT-licensed.

- **Pro:** No third-party font, no license question, ships forever
- **Pro:** Full control over stroke weight, padding, optical alignment
  with the input border
- **Pro:** Tree-shakeable — only icons that are imported land in the
  bundle
- **Pro:** Same shape works at any size (no hinting issues)
- **Con:** Up-front design work — ~12 SVGs to draw + iterate
- **Con:** Won't be pixel-identical to Mendix; "approximate replica"

### Option B — Reach for an OFL-licensed icon font

Phosphor, Material Symbols, Tabler, or Lucide all have decent glyphs for
the math ops. None has the Mendix-style "Ab" text-comparator markers
out of the box.

- **Pro:** No design work for math ops
- **Con:** Mixed visual language — math ops from a third-party font
  alongside hand-drawn "Ab" glyphs would clash
- **Con:** Adds a dependency the project doesn't have today; bundle cost
- **Verdict:** Not worth it given the unicode fallbacks are close enough
  for math ops

### Option C — Source the actual Mendix font

The `datagrid-filters` font is bundled with Mendix's commercial DataGrid 2
widget. Redistributing it likely violates the Mendix Studio Pro license.

- **Pro:** Pixel-perfect QA parity
- **Con:** Legal risk — needs Mendix licensing review before shipping
- **Verdict:** Don't pursue without legal sign-off; even with sign-off,
  Option A delivers 95% of the visual benefit without the risk

### Option D — Status quo (unicode + text)

Keep the current ASCII/unicode mix. Accept the divergence on the four
problem glyphs (Empty / Not empty / Starts with / Ends with) as a
documented LOW-priority gap.

- **Pro:** Zero cost
- **Con:** Reads as "looks like QA in some places" — exactly the
  inconsistency the user flagged

---

## 3. Recommendation

**Option A**, packaged as a single icon component used by every datagrid
filter on the admin shell.

```
frontend/src/components/datagrid/
├── icons/
│   ├── index.ts            (barrel export)
│   ├── ComparatorIcons.tsx (12 SVG components: GreaterThanIcon, EmptyIcon, AbIcon, AbStartsWithIcon, …)
│   └── icons.module.css    (stroke / fill / sizing tokens)
└── Comparator.tsx          (already from comparator-dropdown sprint — adopts these icons)
```

---

## 4. Implementation outline

| Phase | Work                                                                 | Estimate |
|---    |---                                                                   |---       |
| 1     | Take a high-resolution capture of QA's open comparator menus on Reserve Bids + Inventory + Auctions; trace each glyph in Figma or directly in SVG | 0.5 day |
| 2     | Author 12 SVG components in `ComparatorIcons.tsx`; align to a 24 × 24 grid; stroke 1.5 px; use `currentColor` so they inherit text colour | 0.5 day |
| 3     | Replace the ASCII/unicode glyph map in the comparator dropdown's `OP_GLYPHS` with `<Icon />` references | 0.25 day |
| 4     | Visual diff against QA — Playwright screenshots of menu open / closed states on every grid | 0.5 day |
| 5     | Calendar popover — replace the native `<input type="date">` with a custom popover (the comparator-dropdown sprint already needs one for the Last Updated date filter; this phase upgrades it visually) | 0.75 day |
| **Total** |                                                                  | **~2.5 days** |

---

## 5. Risks

| Risk                                                                | Mitigation |
|---                                                                  |---         |
| Hand-drawn glyphs land "approximately" QA-style and reviewers debate the fidelity | Capture QA reference renders in `docs/tasks/qa-vs-local-2026-05-07/icons/` and pin the design as "match within 2 px optical, exact glyph identity not required" |
| Bundle size grows from 12 inline SVGs                              | Each SVG is ~200 bytes after tree-shaking; total <3 KB. Negligible. |
| Other admin pages have their own filter-glyph styles already       | The shared component is opt-in. Existing pages keep their styles until they migrate. |
| Dark mode (if/when it lands) needs separate icons                   | `currentColor` everywhere — automatic. |

---

## 6. Out of scope

- Replacing all admin-app icons (sidebar nav, button leading icons, status
  indicators). This sprint is scoped to filter-comparator + calendar
  glyphs only.
- Animating the glyph swap when the comparator op changes. Static for
  now.
- Right-to-left layout. Defer.

---

## 7. Dependencies

- The comparator-dropdown sprint must land first
  (`docs/tasks/reserve-bids-comparator-dropdown-plan.md`) — the icon
  component slots into its `Comparator.tsx`.
- A reusable calendar popover component for the date filter (Phase 5).
  Check `frontend/src/app/.../purchase-orders/` for an existing date
  picker before building from scratch.

---

## 8. References

- QA comparator menu screenshots:
  - `docs/tasks/qa-vs-local-2026-05-07/reserve-bids/qa-03-bid-comparator-menu.png`
  - `docs/tasks/qa-vs-local-2026-05-07/reserve-bids/qa-04-grade-comparator-menu.png`
- QA calendar popover: `docs/tasks/qa-vs-local-2026-05-07/reserve-bids/qa-05-last-updated-calendar.png`
- Styling spec: `docs/tasks/qa-vs-local-reserve-bids-styling-spec-2026-05-08.md` §5.5 (`font-family: datagrid-filters` flagged as unresolved)
