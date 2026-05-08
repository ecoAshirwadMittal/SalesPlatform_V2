# Purchase Order — Pixel-Parity Styling Spec (2026-05-08)

**Scope:** Token-level styling spec to bring the local Purchase Order surface to **100% visual match** with QA. Companion to [`qa-vs-local-po-walkthrough-2026-05-08.md`](qa-vs-local-po-walkthrough-2026-05-08.md), which covers the structural / functional gaps. This doc is purely about CSS values.

**Method:** All QA values pulled from `getComputedStyle()` against the live QA PO page (not eyeballed from screenshots). Local values pulled the same way against the running local PO list.

---

## 1. Design tokens (use these throughout)

QA's chrome uses a **neutral palette** distinct from local's CLAUDE.md teal spec. The token mapping below preserves local's teal accent for primary CTAs (consistent with the rest of the app) while adopting QA's neutrals for everything else. **Where the two conflict, this doc wins for the PO surface.**

```css
/* PO page tokens — match QA */
--po-bg:              #F7F7F7;   /* page + grid + button background        — rgb(247,247,247) */
--po-text:            #3C3C3C;   /* primary text                            — rgb(60, 60, 60) */
--po-text-muted:      #606671;   /* placeholder / week input value          — rgb(96, 102, 113) */
--po-border:          #D0D0D0;   /* button + filter borders                 — rgb(208, 208, 208) */
--po-divider:         #E0E0E0;   /* row separators (lighter than border)    — derived */

/* Brand accent — kept from CLAUDE.md, used only on primary CTAs */
--po-accent:          #407874;   /* "Create Purchase Order" green pill bg   — local teal */
--po-accent-hover:    #2f5957;   /* darken 10%                              — derived */
--po-accent-text:     #ffffff;

/* Typography */
--po-font:            "Brandon Grotesque", "Open Sans", Arial, sans-serif;
--po-font-icon:       "datagrid-filters";   /* QA's icon font for filter glyphs */
--po-text-base:       16px;     /* body, header cells */
--po-text-sm:         13px;     /* comparator icon-button label */
--po-text-md:         18px;     /* action buttons + filter input */
--po-text-title:      42px;     /* page heading */

/* Border radius */
--po-radius-sm:       3px;      /* buttons, comparator left side */
--po-radius-pill:     999px;    /* status pills (local-only, not used by QA) */

/* Spacing */
--po-pad-btn:         10.8px 18px;   /* Export / Refresh Packout / Import */
--po-pad-cta:         12px 28px;     /* "Create Purchase Order" green pill */
--po-pad-th:          6px 6px 6px 15px;   /* grid header cell */
--po-pad-td:          12px 15px;     /* grid body cell — derived from QA visual */
--po-pad-filter:      2px;            /* filter input inner padding */

/* Heights */
--po-h-action-btn:    44px;     /* action toolbar buttons */
--po-h-filter:        30px;     /* comparator + filter input pair */
--po-h-row:           41px;     /* grid body row (estimated from QA screenshot) */
--po-h-pagination:    42px;
```

---

## 2. Page-level layout

| Element | QA | Local current | Spec |
|---|---|---|---|
| `<body>` bg | `rgb(247, 247, 247)` | `rgb(247, 247, 247)` ✓ | match — no change |
| `<body>` color | `rgb(60, 60, 60)` | `rgb(60, 60, 60)` ✓ | match — no change |
| `<body>` font | `"Brandon Grotesque", sans-serif` | `"Brandon Grotesque", "Open Sans", Arial, sans-serif` | local OK — fallbacks added are harmless |
| `<body>` font-size | `16px` | `16px` ✓ | match — no change |
| Main content padding | (full-bleed; grid touches edges with internal padding) | `20px` | **Local is wrong** — drop to `0` and let inner sections own padding |
| Content max-width | uncapped — grid scrolls horizontally if needed | uncapped ✓ | match |

---

## 3. Page heading ("Purchase Order")

| Property | QA | Local | Spec |
|---|---|---|---|
| Tag | `<h2>` | `<h1>` | **change to `<h2>`** (semantic + CSS reset hook) |
| Text | `Purchase Order` (singular) | `Purchase Orders` (plural) | **rename to singular** (matches walkthrough M11d-style decision) |
| Color | `#3C3C3C` | `#3C3C3C` ✓ | match |
| Font-size | **`42px`** | `16px` | **change to `42px`** |
| Font-weight | `500` | `400` | **change to `500`** |
| Line-height | `54.6px` (`1.3 ratio`) | `24px` | **change to `54.6px`** |
| Letter-spacing | `normal` | `normal` ✓ | match |
| Text-transform | `none` | `none` ✓ | match |

```css
.poHeading {
  font: 500 42px/54.6px var(--po-font);
  color: var(--po-text);
}
```

---

## 4. Action toolbar buttons (Export / Refresh Packout / Import)

QA renders these as **outline-grey buttons** (not filled accents). They sit in the top-right cluster.

| Property | QA Export | QA Refresh Packout | QA Import | Spec |
|---|---|---|---|---|
| Background | `#F7F7F7` | `#F7F7F7` | `#F7F7F7` | `var(--po-bg)` |
| Text color | `#3C3C3C` | `#3C3C3C` | `#3C3C3C` | `var(--po-text)` |
| Border | `1px solid #D0D0D0` | same | same | `1px solid var(--po-border)` |
| Border-radius | `3px` | `3px` | `3px` | `var(--po-radius-sm)` |
| Padding | `10.8px 18px` | `10.8px 18px` | `10.8px 18px` | `var(--po-pad-btn)` |
| Font-size | `18px` | `18px` | `18px` | `var(--po-text-md)` |
| Font-weight | `400` | `400` | `400` | `400` |
| Line-height | `19.8px` | `19.8px` | `19.8px` | `19.8px` |
| Margin-left | `5.4px` | `5.4px` | `5.4px` | `5.4px` (gap between buttons) |
| Width | `107.5px` | `154.1px` | `108.1px` | content-driven (auto) |
| Height | `44.4px` | `43.4px` | `44.4px` | `var(--po-h-action-btn)` |
| Icon | down-arrow ↓ inline before label | (no icon) | up-arrow ↑ inline before label | inline SVG, currentColor stroke |

**Local current** (`+ New PO` button): `bg #407874 (teal)` / white text / `4px` radius / `8px 16px` padding / `16px` font / `40px` height — completely different visual class.

**Decision:** The new toolbar buttons (`Export`, `Refresh Packout`, `Import`) match QA's outline-grey pattern. The existing teal `+ New PO` button **goes away** as part of the rebuild (replaced by the chevron-opens-modal pattern from §6).

```css
.poToolbarBtn {
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  background: var(--po-bg);
  color: var(--po-text);
  border: 1px solid var(--po-border);
  border-radius: var(--po-radius-sm);
  padding: 10.8px 18px;
  height: var(--po-h-action-btn);
  font: 400 18px/19.8px var(--po-font);
  margin-left: 5.4px;
  cursor: pointer;
}
.poToolbarBtn:hover { background: #ebebeb; }
.poToolbarBtn:disabled { opacity: 0.5; cursor: not-allowed; }
.poToolbarBtn svg { width: 14px; height: 14px; stroke: currentColor; }
```

---

## 5. Week-range selector

A combobox with three interactive zones:

```
┌──────────────────────────────────────┬──┬──┐
│ 2026/21-2026/27                       │ X│ ▾│  ← clear · open menu
└──────────────────────────────────────┴──┴──┘   ← right of this whole pill: ▼ "Create PO" modal trigger
```

| Property | QA | Spec |
|---|---|---|
| Outer container border | `1px solid #606671` | `1px solid var(--po-text-muted)` (yes, darker than chrome border) |
| Outer container radius | `3px` | `var(--po-radius-sm)` |
| Outer container bg | `#F7F7F7` | `var(--po-bg)` |
| Width | ~280px | `min-width: 280px` |
| Height | `26px` (input itself) | `var(--po-h-filter)` for the wrapper |
| Input value color | `#606671` (muted slate) | `var(--po-text-muted)` |
| Input value font | `18px / 25.7px` Brandon | match |
| Clear `X` icon | small grey | inline SVG, 14×14, currentColor |
| Right chevron `▾` | small grey, opens week range list | inline SVG |
| **Adjacent ▼ button** (the Create PO modal trigger — separate from the selector) | small `36×36` grey button, matches toolbar btn styling | reuse `.poToolbarBtn` size variant |

```css
.poWeekRange {
  display: inline-flex;
  align-items: center;
  height: var(--po-h-filter);
  min-width: 280px;
  padding: 0 8px;
  background: var(--po-bg);
  border: 1px solid var(--po-text-muted);
  border-radius: var(--po-radius-sm);
  color: var(--po-text-muted);
  font: 400 18px/25.7px var(--po-font);
}
.poWeekRangeInput {
  flex: 1;
  border: 0;
  background: transparent;
  color: inherit;
  font: inherit;
  outline: none;
}
.poWeekRangeIconBtn {
  display: inline-flex;
  align-items: center;
  justify-content: center;
  width: 18px;
  height: 18px;
  padding: 0;
  background: none;
  border: 0;
  color: var(--po-text-muted);
  cursor: pointer;
}
```

---

## 6. Create / Update PO modal

Triggered by the small ▼ button next to the week-range selector.

| Element | QA | Spec |
|---|---|---|
| Backdrop | `rgba(0, 0, 0, ~0.4)` (dark semi-transparent) | match |
| Modal card | white bg, ~600px wide, centered | `bg #fff`, `max-width: 600px`, `border-radius: 6px`, `box-shadow: 0 6px 24px rgba(0,0,0,0.15)` |
| Heading | "Create/Update Purchase Order", left-aligned | `font: 500 24px/1.3 var(--po-font); color: var(--po-text);` |
| Close X (top-right) | grey, no border | inline SVG button, `color: #888`, hover darken |
| Form labels | "From Week", "To Week" — left of dropdowns | `font: 400 16px var(--po-font); color: var(--po-text-muted);` |
| Dropdowns | full-width, grey-bordered | reuse `.poWeekRange` styling |
| File picker row | textbox-style placeholder + "Browse..." button | text input `var(--po-bg)` bg, "Browse..." as outline button |
| Hint text | "Supported format: .xlsx" | `font-size: 13px; color: var(--po-text-muted);` |
| **CTA: Create Purchase Order** | green pill, white text, centered, ~250px wide | `background: var(--po-accent); color: #fff; padding: 12px 28px; border-radius: 999px; font: 500 18px var(--po-font);` |

**Local current:** the equivalent flow is the broken `/new` page with placeholder text — replace entirely (per walkthrough §6).

---

## 7. Grid

### 7.1 Container

| Property | QA | Spec |
|---|---|---|
| Background | `#F7F7F7` (transparent against page) | `var(--po-bg)` |
| Border | `0px none` | `none` |
| Box-shadow | `none` | `none` |
| Outer padding | none — grid sits flush within page padding | none |

### 7.2 Header row (column titles)

| Property | QA | Spec |
|---|---|---|
| Cell background | `#F7F7F7` | `var(--po-bg)` |
| Cell color | `#3C3C3C` | `var(--po-text)` |
| Cell padding | `6px 6px 6px 15px` | match |
| Cell margin-top | `6px` (separates from anything above) | match |
| Cell border | `1px solid #D0D0D0` (top + bottom only — vertical dividers absent) | `border-top: 1px solid var(--po-border); border-bottom: 1px solid var(--po-border);` |
| Cell height | `~70px` (label + filter row stacked inside) | content-driven |
| Label font | `400 16px/22.86px Brandon` | match |
| Sort arrow icons | small chevron pair (up + down), 16px, `#3C3C3C`, vertical stack | inline SVG |

### 7.3 Body rows

| Property | QA (estimated from screenshot — body cells weren't queryable on the empty grid) | Spec |
|---|---|---|
| Row background | white-ish (slightly lighter than page bg, no stripes observed) | `#fff` |
| Row border-bottom | `1px solid #E0E0E0` (very subtle divider) | `var(--po-divider)` |
| Row hover | light grey tint | `background: #f0f0f0;` |
| Row height | ~41px | `var(--po-h-row)` |
| Cell padding | `12px 15px` (estimated) | `var(--po-pad-td)` |
| Cell font | `400 16px Brandon`, color `#3C3C3C` | match |

```css
.poGrid {
  width: 100%;
  border-collapse: collapse;
  background: var(--po-bg);
  font: 400 16px/1.4 var(--po-font);
  color: var(--po-text);
}
.poGrid thead th {
  background: var(--po-bg);
  border-top: 1px solid var(--po-border);
  border-bottom: 1px solid var(--po-border);
  padding: 6px 6px 6px 15px;
  margin-top: 6px;
  font-weight: 400;
  text-align: left;
  vertical-align: top;
  color: var(--po-text);
}
.poGrid thead th .label {
  display: inline-flex;
  align-items: center;
  gap: 0.4rem;
  font-size: 16px;
  line-height: 22.86px;
}
.poGrid thead th .sortArrows {
  display: inline-flex;
  flex-direction: column;
  width: 14px;
  margin-left: auto;
}
.poGrid tbody tr {
  border-bottom: 1px solid var(--po-divider);
  background: #fff;
}
.poGrid tbody tr:hover { background: #f0f0f0; }
.poGrid tbody td { padding: 12px 15px; }
```

---

## 8. Filter row (comparator + textbox)

The filter row sits inside each header cell, **below** the column label. Comparator + textbox are visually joined.

```
ProductID  ⇅
┌──┬─────────┐
│ =│         │     ← comparator button | textbox
└──┴─────────┘
```

### 8.1 Comparator button (left)

| Property | QA | Spec |
|---|---|---|
| Background | `#F7F7F7` | `var(--po-bg)` |
| Text color | `#3C3C3C` | `var(--po-text)` |
| Border | `1px solid #D0D0D0` (left + top + bottom; right side joins the input) | (composite — see CSS below) |
| Border-radius | `3px 0 0 3px` (left-rounded only) | match |
| Padding | `6.5px` | match |
| Width | `37.25px` | match |
| Height | `30px` | `var(--po-h-filter)` |
| Font-family | `datagrid-filters` (icon font) | inline SVG fallback (we won't ship Mendix's icon font) |
| Font-size | `13px` | match |

### 8.2 Filter input (right)

| Property | QA | Spec |
|---|---|---|
| Background | `#F7F7F7` | `var(--po-bg)` |
| Text color | `#3C3C3C` | `var(--po-text)` |
| Border | `1px solid #D0D0D0` (top + right + bottom; left joins the comparator) | (composite — see CSS below) |
| Border-radius | `0` (no rounding) | match |
| Padding | `2px` | match |
| Width | `~81px` (column-width-driven) | `min-width: 80px; flex: 1` |
| Height | `30px` | `var(--po-h-filter)` |
| Font-size | `18px` | match |

```css
.poFilterRow {
  display: flex;
  align-items: stretch;
  height: var(--po-h-filter);
}
.poFilterRow .comparator {
  width: 37.25px;
  padding: 6.5px;
  background: var(--po-bg);
  color: var(--po-text);
  border: 1px solid var(--po-border);
  border-right: 0;
  border-radius: var(--po-radius-sm) 0 0 var(--po-radius-sm);
  font-size: 13px;
  cursor: pointer;
  display: inline-flex;
  align-items: center;
  justify-content: center;
}
.poFilterRow .comparator svg { width: 12px; height: 12px; stroke: currentColor; }
.poFilterRow .input {
  min-width: 80px;
  flex: 1;
  padding: 2px;
  background: var(--po-bg);
  color: var(--po-text);
  border: 1px solid var(--po-border);
  border-radius: 0;
  font: 400 18px/25.7px var(--po-font);
  outline: none;
}
.poFilterRow .input:focus {
  border-color: var(--po-accent);
  background: #fff;
}
```

### 8.3 Comparator dropdown menu

Opens below the comparator button when clicked.

| Property | QA (from screenshot inspection) | Spec |
|---|---|---|
| Container bg | `#fff` | match |
| Container border | `1px solid #D0D0D0` | `var(--po-border)` |
| Container border-radius | `3px` | `var(--po-radius-sm)` |
| Container box-shadow | subtle drop shadow `0 4px 12px rgba(0,0,0,0.08)` | match |
| Min width | ~150px (numeric) / ~170px (text — fits "Greater than or equal") | match |
| Item padding | `8px 12px` | match |
| Item font | `400 14px Brandon` | match |
| Item icon (left of label) | small SVG/glyph for the operator (`>`, `=`, `≥`, etc.) | inline SVG, 12×12 |
| Selected item color | green `var(--po-accent)` | match — bold + green |
| Hover bg | `#f5f5f5` | match |

**Numeric column options (8):** Greater than · Greater than or equal · Equal · Not equal · Smaller than · Smaller than or equal · Empty · Not empty.

**Text column options (11):** Contains · Starts with · Ends with · Greater than · Greater than or equal · Equal · Not equal · Smaller than · Smaller than or equal · Empty · Not empty.

---

## 9. Pagination

| Property | QA | Spec |
|---|---|---|
| Position | bottom-right of grid | `display: flex; justify-content: flex-end;` |
| Buttons | First (⏮) · Prev (◀) · "1 to 5 of 5" text · Next (▶) · Last (⏭) | match |
| Button bg | transparent (no fill) | `transparent` |
| Button color | `#3C3C3C` | `var(--po-text)` |
| Button width / height | `34px × 41.7px` | match |
| Button padding | `6px` | match |
| Button border | `1px solid transparent` (visible only on focus) | match |
| Button border-radius | `3px` | `var(--po-radius-sm)` |
| Disabled state | greyed icon, no border on hover | `opacity: 0.4; cursor: default;` |
| Count text | `font: 400 16px Brandon`, color `#3C3C3C` | match |
| Gap | small space between buttons + count | `gap: 4px;` |

**Local current bug (PO-13):** "Showing 5 of 5.PrevNext" renders as a single run-on string. Replace with a proper flex container per the spec above.

```css
.poPagination {
  display: flex;
  align-items: center;
  justify-content: flex-end;
  gap: 4px;
  height: var(--po-h-pagination);
  padding: 0.5rem;
}
.poPagination button {
  width: 34px;
  height: 34px;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  background: transparent;
  border: 1px solid transparent;
  border-radius: var(--po-radius-sm);
  color: var(--po-text);
  cursor: pointer;
}
.poPagination button:hover { background: #ebebeb; }
.poPagination button:disabled { opacity: 0.4; cursor: default; }
.poPagination .count {
  margin: 0 0.5rem;
  font: 400 16px var(--po-font);
  color: var(--po-text);
}
```

---

## 10. Column visibility selector ("eye" button + menu)

| Property | QA | Spec |
|---|---|---|
| Trigger | small eye icon at far right of header row | inline SVG, 18×18, `var(--po-text)` |
| Menu container | white card, subtle shadow, ~180px wide | `bg #fff`, `border 1px solid var(--po-border)`, `border-radius 3px`, `box-shadow 0 4px 12px rgba(0,0,0,0.08)` |
| Menu item | green checkbox + label | `display: flex; align-items: center; gap: 0.5rem; padding: 8px 12px;` |
| Checkbox | green-filled when checked | accent-color `var(--po-accent)` |
| Item label | `400 14px Brandon` | match |

Local has no equivalent — net-new component.

---

## 11. "Fulfilled as of: EST" indicator

A tiny right-aligned text strip below the action toolbar.

| Property | QA | Spec |
|---|---|---|
| Container | inline | `display: inline-flex; gap: 0.4rem;` |
| Label "Fulfilled as of:" | muted grey | `color: var(--po-text-muted);` |
| Value (timestamp + "EST") | bolder `#3C3C3C` | `color: var(--po-text); font-weight: 500;` |
| Font-size | `13px` | match |
| Position | aligned to right of the row, beneath the action buttons | `text-align: right; margin-top: 4px;` |

Local has no equivalent — net-new (depends on `Refresh Packout` port surfacing the timestamp).

---

## 12. Status pills (deprecated for the rebuild)

QA's PO grid does **not** render status pills (state is week-range derived, not stored). Local currently shows `CLOSED` / `ACTIVE` pills on the list page; per the walkthrough §1 recommendation, the list page goes away in favor of the grid + week-range filter. Status pills can be removed from the rebuild entirely.

If we keep them on the auctions list page (different surface), the existing styling (`bg #888`, `radius 999px`, `13.6px` font) is fine — out of scope for this doc.

---

## 13. Local-only side fixes (low effort, do alongside)

These are styling bugs visible on the *current* local PO list page that should be cleaned up regardless of the rebuild:

| # | Bug | Fix |
|---|---|---|
| S1 | Page heading rendered as `<h1>` with body-text styling (`16px / 400`) | Use `<h2>` with `42px / 500 / 54.6px line-height` per §3 |
| S2 | "Purchase Orders" plural | Rename to "Purchase Order" singular |
| S3 | Pagination footer renders as run-on `"Showing 5 of 5.PrevNext"` | Wrap in flex container with `gap` between count + buttons |
| S4 | Status pill "CLOSED" uses `rgb(136, 136, 136)` mid-grey — low contrast against the page bg | Bump to `#606671` (darker slate) or use the dark teal `#112d32` per CLAUDE.md |
| S5 | Edit / Delete action links lack underline + hover treatment | `text-decoration: underline; color: var(--po-accent);` for Edit, red `#a31b1b` for Delete |
| S6 | `main` content has `padding: 20px` — fights against the QA flush-to-edge layout | Remove the wrapper padding, let inner sections own padding |

---

## 14. Implementation checklist (do these in this order)

When the PO rebuild lands (per walkthrough Sprint A/B/C), apply tokens + CSS modules in this sequence:

- [ ] **Tokens.** Add the `--po-*` variables to `frontend/src/app/(dashboard)/admin/auctions-data-center/purchase-orders/po.module.css` (or a new `po-tokens.css` shared file).
- [ ] **Page heading + container.** Update title to `<h2>` + apply §3 font tokens.
- [ ] **Action toolbar.** Build `.poToolbarBtn` with the §4 spec; render Export / Refresh Packout / Import.
- [ ] **Week-range selector.** Composite component per §5.
- [ ] **Create modal.** Single modal per §6, replacing the broken `/new` page.
- [ ] **Grid component.** Container + header cells + body rows per §7.
- [ ] **Filter row.** Comparator + input pair per §8.1, §8.2.
- [ ] **Comparator dropdown menu.** Per §8.3 — supports both 8-option (numeric) and 11-option (text) sets.
- [ ] **Pagination.** Per §9. Removes the existing run-on PO-13 bug.
- [ ] **Column visibility menu.** Per §10.
- [ ] **"Fulfilled as of" indicator.** Per §11. Wire to the Refresh Packout response.
- [ ] **Cleanup local-only side fixes** (§13 S1–S6) — can ship before the rebuild.

---

## 15. What's intentionally NOT matched (divergences we keep)

| | QA | Local | Decision |
|---|---|---|---|
| Sidebar palette | solid green | teal gradient `#407874` per CLAUDE.md | **Keep local** — this is documented in `docs/architecture/decisions.md` ADR L21 |
| Buyer User Guide sidebar link | present | added M12a | **Keep local** |
| Status pills on list page | absent | retained until grid rebuild | **Keep local** during transition |
| Browser tab title | `"ecoATM Direct - Create PO"` | `"ecoATM Sales Platform"` | **Soft-match** — set tab title to `"Purchase Order — ecoATM Sales Platform"` |
| Mendix `datagrid-filters` icon font | shipped | not shipped | **Inline SVG** instead — same shapes, no font dep |

---

## 16. Reference

- Walkthrough doc: [`qa-vs-local-po-walkthrough-2026-05-08.md`](qa-vs-local-po-walkthrough-2026-05-08.md)
- Screenshots: `docs/tasks/qa-vs-local-2026-05-07/po/`
- Computed-style extraction method: `getComputedStyle()` via Playwright `browser_evaluate`, against the live QA + local pages on 2026-05-08
- Brand reference: `Brandon_Grotesque.css` in repo root (already shipped — don't add a separate font dep)
