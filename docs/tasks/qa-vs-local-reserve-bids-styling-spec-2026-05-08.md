# Reserve Bids (EB) — Pixel-Parity Styling Spec (2026-05-08)

**Scope:** All five Reserve Bids routes — list, upload, new, detail/edit, audit.
**Method:** All QA values pulled from `getComputedStyle()` against the live QA page
(`https://buy-qa.ecoatmdirect.com/index.html` after navigating to "Reserved Bids (EB)").
Raw values in `qa-styles.json` (this directory).
**Companion:** [Walkthrough doc](qa-vs-local-reserve-bids-walkthrough-2026-05-08.md)
defines what to build; this doc defines what it should look like.

> **Heads-up on brand color drift.** QA's primary teal computes to `rgb(0, 120, 127)` =
> `#00787F`. CLAUDE.md documents the brand teal as `#407874`. Decision below in §11. Until
> resolved, this spec uses the **QA-actual** color so the new build literally matches the
> screenshots.

---

## 1. Design tokens

```css
:root {
  /* === Color === */
  --rb-bg:                #F7F7F7;          /* page background — body */
  --rb-text:              #3C3C3C;          /* primary text (rgb 60,60,60) */
  --rb-border:            #D0D0D0;          /* table + input + button outline */
  --rb-border-strong:     #3C3C3C;          /* comparator-button right edge (active state) */
  --rb-primary:           #00787F;          /* QA teal — Upload button, active sort */
  --rb-primary-text:      #FFFFFF;          /* on-primary */
  --rb-error:             #C0392B;          /* Internal server error alert (best guess; QA didn't render this) */
  --rb-row-hover:         rgba(0, 120, 127, 0.06); /* derived; QA hover not exercised */
  --rb-sidebar-bg:        linear-gradient(180deg, #1A6B33 0%, #0F7E28 100%); /* keep local green */

  /* === Typography === */
  --rb-font:              "Brandon Grotesque", "Open Sans", Arial, sans-serif;
  --rb-text-base:         16px;             /* body */
  --rb-text-input:        18px;             /* filter input + buttons */
  --rb-text-comparator:   13px;             /* comparator button (icon-font) */
  --rb-text-heading:      42px;             /* H2 page heading */
  --rb-line-base:         1.43;             /* 22.8571/16 — table cells, sidebar items */
  --rb-line-heading:      1.3;              /* 54.6/42 */
  --rb-weight-regular:    400;
  --rb-weight-heading:    500;
  --rb-weight-grid-header:400;              /* QA — grid headers are NOT bold */

  /* === Spacing === */
  --rb-pad-button:        10.8px 18px;      /* top-bar buttons */
  --rb-pad-header-cell:   6px 6px 6px 15px; /* grid columnheader */
  --rb-pad-body-cell:     6px 19px;         /* grid gridcell */
  --rb-pad-filter:        2px;              /* filter input padding */
  --rb-pad-comparator:    6.5px;            /* comparator button square */
  --rb-pad-pagination:    6px;              /* pagination icon button */

  /* === Sizing === */
  --rb-h-button:          44.39px;          /* top-bar buttons */
  --rb-h-filter:          30px;             /* filter input + comparator */
  --rb-h-grid-header:     73.84px;          /* two-line header (label + filter row) */
  --rb-w-comparator:      37.25px;          /* comparator square */
  --rb-w-pagination:      34px;             /* pagination icon button */
  --rb-h-pagination:      41.7px;
  --rb-w-sidebar:         232px;
  --rb-h-sidebar-item:    64px;

  /* === Radius === */
  --rb-radius-sm:         3px;              /* buttons, comparator-left-side */
  --rb-radius-comparator: 3px 0 0 3px;      /* sits flush-left of input */

  /* === Icon font (for filter comparators) === */
  --rb-icon-font:         "datagrid-filters";
}
```

---

## 2. Page-level layout

| Element                        | QA                                          | Local current                            | Spec to apply                            |
|---                             |---                                          |---                                       |---                                       |
| `body` background              | `#F7F7F7`                                   | `#F7F7F7` ✓                              | Already correct                          |
| `body` text                    | `#3C3C3C`                                   | `#3C3C3C` ✓                              | Already correct                          |
| `body` font                    | `"Brandon Grotesque", sans-serif` (16px)    | `"Brandon Grotesque", "Open Sans", Arial, sans-serif` (16px) ✓ | Local is correct (richer fallback chain) |
| Main content top padding       | ~28px (visual, not measured)                | unstyled                                 | `padding-block-start: clamp(20px, 2vw, 32px)` |
| Main content side padding      | ~32px (visual)                              | unstyled                                 | `padding-inline: clamp(24px, 3vw, 40px)` |

---

## 3. Page heading

| Property         | QA actual              | Local current          | Spec to apply        |
|---               |---                     |---                     |---                   |
| Tag              | `H2`                   | `H1`                   | **`H2`** (matches QA — H1 should be reserved for app shell if used at all) |
| `font-family`    | `Brandon Grotesque`    | `Brandon Grotesque`    | unchanged            |
| `font-size`      | **42px**               | 16px                   | `42px`               |
| `font-weight`    | 500                    | 400                    | `500`                |
| `line-height`    | 54.6px                 | 24px                   | `54.6px` (= `1.3`)   |
| `color`          | `#3C3C3C`              | `#3C3C3C`              | unchanged            |
| Bottom border    | 1px horizontal divider below heading | none     | `border-bottom: 1px solid var(--rb-border)`; `padding-block-end: 12px`; `margin-block-end: 16px` |

```css
.reserveBidsHeading {
  font-family: var(--rb-font);
  font-size: var(--rb-text-heading);
  font-weight: var(--rb-weight-heading);
  line-height: var(--rb-line-heading);
  color: var(--rb-text);
  border-block-end: 1px solid var(--rb-border);
  padding-block-end: 12px;
  margin-block-end: 16px;
}
```

---

## 4. Top-bar action row

Layout: heading on the left, button group on the right of the same row, vertically centered.

### 4.1 Secondary button (`Download`)

| Property         | QA actual                   | Local current               | Spec |
|---               |---                          |---                          |---   |
| `width`          | 130.45px (auto)             | 99.9px (text width)         | `auto`                |
| `height`         | 44.39px                     | 24px                        | `44.39px`             |
| `padding`        | `10.8px 18px`               | 0                           | `var(--rb-pad-button)`|
| `background`     | `#F7F7F7`                   | transparent                 | `var(--rb-bg)`        |
| `color`          | `#3C3C3C`                   | `#3C3C3C`                   | unchanged             |
| `border`         | `1px solid #D0D0D0`         | none                        | `1px solid var(--rb-border)` |
| `border-radius`  | 3px                         | 0                           | `var(--rb-radius-sm)` |
| `font-size`      | 18px                        | 16px                        | `var(--rb-text-input)`|
| `font-weight`    | 400                         | 400                         | unchanged             |
| Leading icon     | download arrow ▼            | none                        | Lucide `Download` 18px stroke 1.5 |
| Margin-left      | 5.4px (when grouped)        | 0                           | `5.4px`               |

### 4.2 Primary button (`Upload EB Price`)

| Property         | QA actual                   | Local current               | Spec |
|---               |---                          |---                          |---   |
| `background`     | `#00787F` (QA teal)         | transparent                 | `var(--rb-primary)`   |
| `color`          | `#FFFFFF`                   | `#3C3C3C`                   | `var(--rb-primary-text)` |
| `border`         | `1px solid #00787F`         | none                        | `1px solid var(--rb-primary)` |
| Leading icon     | upload arrow ▲              | none                        | Lucide `Upload` 18px |
| Other            | identical to secondary above |                            | (inherits)            |

### 4.3 The `/new` button — DO NOT STYLE

Per RB-3 / RB-21 in the walkthrough, this control should be removed unless its addition
is justified in an ADR. If kept, treat as a tertiary (text-only) action with a chevron-
right glyph; otherwise drop entirely.

```css
.rbBtn {
  display: inline-flex;
  align-items: center;
  gap: 8px;
  height: var(--rb-h-button);
  padding: var(--rb-pad-button);
  font-family: var(--rb-font);
  font-size: var(--rb-text-input);
  font-weight: var(--rb-weight-regular);
  border-radius: var(--rb-radius-sm);
  cursor: pointer;
}

.rbBtn--secondary {
  background: var(--rb-bg);
  color: var(--rb-text);
  border: 1px solid var(--rb-border);
}

.rbBtn--primary {
  background: var(--rb-primary);
  color: var(--rb-primary-text);
  border: 1px solid var(--rb-primary);
}

.rbBtn + .rbBtn {
  margin-inline-start: 5.4px;
}
```

---

## 5. Data grid

### 5.1 Container

| Property         | QA actual                   | Local current               | Spec |
|---               |---                          |---                          |---   |
| `background`     | `#F7F7F7` (no fill, body bleed) | transparent             | `transparent`         |
| `border`         | none                        | 0 solid                     | `0`                   |
| `border-radius`  | 0                           | 0                           | `0`                   |
| Cell separator   | 1px `#D0D0D0` between rows  | none                        | `border-block-end: 1px solid var(--rb-border)` on each row |

### 5.2 Header cell

| Property         | QA actual                   | Local current               | Spec |
|---               |---                          |---                          |---   |
| `height`         | 73.84px                     | 24px                        | `var(--rb-h-grid-header)` (two-line: label + filter) |
| `padding`        | `6px 6px 6px 15px`          | 0                           | `var(--rb-pad-header-cell)` |
| `font-size`      | 16px                        | 16px                        | unchanged             |
| `font-weight`    | 400                         | 700                         | **`400`** (QA grid headers are NOT bold) |
| `background`     | `#F7F7F7`                   | transparent                 | `var(--rb-bg)`        |
| `color`          | `#3C3C3C`                   | `#3C3C3C`                   | unchanged             |
| Bottom border    | 1px `#D0D0D0`               | none                        | `1px solid var(--rb-border)` |
| Sort triangle    | 8px arrows-alt-v glyph from `fas` | none                  | FontAwesome or Lucide `ArrowUpDown`, 12px |

### 5.3 Body cell

| Property         | QA actual                   | Local current               | Spec |
|---               |---                          |---                          |---   |
| `padding`        | `6px 19px`                  | 0                           | `var(--rb-pad-body-cell)` |
| `font-size`      | 16px                        | 16px                        | unchanged             |
| `line-height`    | 22.86px (`1.43`)            | 24px                        | `var(--rb-line-base)` |
| Numeric cols     | right-aligned (Product ID, Bid) | left                    | `text-align: end`     |

### 5.4 Filter input (per-column)

| Property         | QA actual                   | Local current               | Spec |
|---               |---                          |---                          |---   |
| `width`          | 121.75px (auto-fit column)  | 152px (above grid)          | `100%` of column inner width |
| `height`         | 30px                        | 24px                        | `var(--rb-h-filter)`  |
| `padding`        | 2px                         | 0                           | `var(--rb-pad-filter)`|
| `font-size`      | 18px                        | 16px                        | `var(--rb-text-input)`|
| `border`         | `1px solid #D0D0D0`         | 0                           | `1px solid var(--rb-border)` |
| `border-radius`  | 0 (right side; comparator owns the left) | 0              | `0 var(--rb-radius-sm) var(--rb-radius-sm) 0` |
| `background`     | `#F7F7F7`                   | transparent                 | `var(--rb-bg)`        |

### 5.5 Comparator combobox button

| Property         | QA actual                   | Local current               | Spec |
|---               |---                          |---                          |---   |
| `width`          | **37.25px** (exact)         | n/a                         | `var(--rb-w-comparator)` |
| `height`         | 30px                        | n/a                         | `var(--rb-h-filter)`  |
| `padding`        | 6.5px                       | n/a                         | `var(--rb-pad-comparator)` |
| `font-family`    | `datagrid-filters` (icon font!) | n/a                     | `var(--rb-icon-font)` — must source the icon font from Mendix bundle, OR substitute Lucide glyphs (`Equal`, `ChevronDown`, …) and adjust width |
| `font-size`      | 13px                        | n/a                         | `var(--rb-text-comparator)` |
| `border-radius`  | `3px 0 0 3px`               | n/a                         | `var(--rb-radius-comparator)` |
| Right edge       | `1px solid #3C3C3C` (darker) | n/a                        | `border-inline-end: 1px solid var(--rb-border-strong)` |
| Outer borders    | `1px solid #D0D0D0`         | n/a                         | matches filter input |

### 5.6 Comparator menu (numeric)

8 options in this order (matches QA `qa-03-bid-comparator-menu.png`):

```
> Greater than
≥ Greater than or equal
= Equal              (default)
≠ Not equal
< Smaller than
≤ Smaller than or equal
=̄ Empty
≠̄ Not empty
```

### 5.7 Comparator menu (text — for Grade, Brand, Model Name, Changed By)

11 options in this order (matches QA `qa-04-grade-comparator-menu.png`):

```
Ab Contains          (default)
Ab Starts with
Ab Ends with
+ all 8 numeric ops above
```

### 5.8 Date filter (Last Updated, Changed On)

- 8 numeric comparator options + a calendar trigger (`Show calendar` button) right of the input
- Calendar popover anchored below the input
- **Decision required**: QA's Mendix DG2 calendar starts the week on **Thursday** (Th, Fr, Sa, Su, Mo, Tu, We). This is unusual but matches what QA users see today. Ship the popover with **Sunday** start (US convention) and call this an intentional improvement, OR mirror QA exactly — your call. Recommend Sunday-start with explicit ADR.
- Header has month dropdown + year dropdown (range yet to be decided — 5 years back, 1 forward seems reasonable)

---

## 6. Pagination

| Property         | QA actual                   | Local current               | Spec |
|---               |---                          |---                          |---   |
| Layout           | `<<` `<` "1 to 20 of 14659" `>` `>>` | "Prev / Page 1 of 1 / Next" | First / Prev / count / Next / Last |
| Button width     | 34px                        | text-width                  | `var(--rb-w-pagination)` |
| Button height    | 41.7px                      | text                        | `var(--rb-h-pagination)` |
| Button padding   | 6px                         | 0                           | `var(--rb-pad-pagination)` |
| Background       | transparent                 | transparent                 | unchanged |
| Border           | transparent                 | none                        | `1px solid transparent` (so `:hover`/`:focus` can swap to `var(--rb-border)`) |
| Border-radius    | 3px                         | 0                           | `var(--rb-radius-sm)` |
| Disabled state   | reduced opacity, non-interactive | greyed out             | `opacity: 0.4; cursor: not-allowed` |
| Counter format   | `Currently showing N to M of T` (sr-only) + `N to M of T` (visible) | `Page X of Y` | `N to M of T` visible; sr-only equivalent for screen readers |

---

## 7. Audit modal

Replaces the local `[id]/audit` route per RB-14. Use a centered modal anchored to the row click.

| Property         | QA actual                   | Spec |
|---               |---                          |---   |
| Width            | ~760px                      | `min(760px, 90vw)` |
| Background       | white                       | `#FFFFFF` |
| Border-radius    | ~6px                        | `6px`  |
| Box-shadow       | drop-shadow on dimmed backdrop | `0 24px 48px rgba(0,0,0,0.18)` |
| Backdrop         | `rgba(0,0,0,0.32)` overlay  | matches |
| Title            | "Audit" H4                  | `H4 Brandon Grotesque 24px / 500` |
| Close button     | `×` top-right, 24px         | matches |
| Columns          | Old price / New price / Changed On / Changed By | drop the Product/Grade columns local currently has |

---

## 8. Upload modal

Replaces the `/upload` route per RB-18.

| Element          | QA actual                   | Spec |
|---               |---                          |---   |
| Width            | ~520px                      | `min(520px, 90vw)` |
| Title            | "Upload File" H4            | match (consider "Upload Reserve Bids" — clearer) |
| Body             | text input (read-only) + Browse + Upload | match, plus add: |
| Add: template DL | (none in QA)                | "Download Excel template" link below the file input — addresses RB-19 polish without QA divergence |
| Add: validation  | (none in QA)                | post-upload row count + error list (e.g. "412 rows added, 3 rows rejected: see details") |

---

## 9. Sidebar — keep as-is

Local's green-gradient sidebar is intentional per CLAUDE.md token brief. **No changes.**

The active item should still display the `rgba(255, 255, 255, 0.18)` overlay that matches
existing local pages — verify after the list page rebuild.

---

## 10. Local-only side fixes (quick wins, ship today)

| #  | Bug                                                  | Fix                                                                            |
|--- |---                                                   |---                                                                             |
| 1  | "Internal server error" alert is bare red text       | Use existing `<Alert role="alert" variant="error">` shell with icon            |
| 2  | "Edit Reserve Bid #73" exposes internal PK           | Show `Reserve Bid for Product #7128` in the heading; keep `id` only in URL    |
| 3  | "Audit Trail — Reserve Bid #73" same issue           | Match: `Audit Trail — Product #7128`                                          |
| 4  | "Model" column header should be "Model Name"         | One-line label change                                                          |
| 5  | "Actions" column header should be "Audit"            | One-line label change                                                          |
| 6  | "Old"/"New" → "Old price"/"New price" on audit page  | One-line label change                                                          |
| 7  | "When" column → "Changed On"                         | One-line label change                                                          |
| 8  | Filter input placeholders end in `...` truncation    | Use `Filter productId…` (single ellipsis Unicode, not 3 dots)                  |

These are independent of the rebuild and can land in a separate PR.

---

## 11. Intentional divergences (cross-link from ADRs)

| Item                                                | QA                          | Local                          | Decision |
|---                                                  |---                          |---                             |---       |
| Brand teal value                                    | `#00787F` (rgb 0,120,127)   | `#407874` per CLAUDE.md         | **Use QA value (`#00787F`) on this page** so it pixel-matches; track an ADR to align the global token across all admin pages, or revise CLAUDE.md to reflect the QA value. Until then, do NOT introduce both colors on the same screen. |
| Sidebar background                                  | white                       | green gradient                  | Keep local green — this is a deliberate brand refresh, not a bug |
| Top-bar user identity                               | none                        | "Admin User" + AU avatar        | Keep local — strict improvement, no ADR needed |
| `/new` manual-create route                          | not present                 | exists                          | **Remove** unless an ADR justifies it; legacy invariant is "EB authored only via Excel" |
| Audit and Upload as separate routes                 | modals                      | routes                          | **Convert to modals** — matches user expectation and keeps grid context |
| Calendar popover weekday-start day                  | Thursday (Mendix DG2)       | n/a                             | Implement as Sunday-start; record ADR if QA users push back |

---

## 12. Implementation checklist

- [ ] Tokens block landed in `frontend/src/styles/tokens.css` (or page-scoped CSS module)
- [ ] H2 heading replaces H1, applies 42px/500
- [ ] Top-bar buttons use `.rbBtn--primary` / `.rbBtn--secondary`
- [ ] `/new` route either deleted or wrapped in an ADR
- [ ] List grid uses TanStack Table (or AG Grid) skinned to QA values
- [ ] Per-column filter row + comparator combobox component
- [ ] Numeric / text / date comparator menus expose the exact 8 / 11 / 8+calendar option lists
- [ ] Column-selector eye icon dropdown
- [ ] Pagination footer uses `N to M of T` format
- [ ] Audit modal replaces the `/[id]/audit` route
- [ ] Upload modal replaces the `/upload` route
- [ ] Edit page either implements real form OR is removed in favor of inline edit
- [ ] Local-only side fixes (§10) shipped as a separate PR

---

## 13. References

- Walkthrough doc (gap inventory + sprint plan): [`qa-vs-local-reserve-bids-walkthrough-2026-05-08.md`](qa-vs-local-reserve-bids-walkthrough-2026-05-08.md)
- Audit playbook (methodology): [`qa-vs-local-page-audit-playbook.md`](qa-vs-local-page-audit-playbook.md)
- PO styling-spec exemplar (same shape): [`qa-vs-local-po-styling-spec-2026-05-08.md`](qa-vs-local-po-styling-spec-2026-05-08.md)
- QA computed-styles raw capture: [`qa-vs-local-2026-05-07/reserve-bids/qa-styles.json`](qa-vs-local-2026-05-07/reserve-bids/qa-styles.json)
- Local computed-styles raw capture: [`qa-vs-local-2026-05-07/reserve-bids/local-styles.json`](qa-vs-local-2026-05-07/reserve-bids/local-styles.json)
- ADR template: [`docs/adr/template.md`](../adr/template.md)
