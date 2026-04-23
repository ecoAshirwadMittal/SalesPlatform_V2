# UI Primitives

Phase 0 design tokens and button primitives for the wholesale buyer portal.
All classes and tokens are defined in `frontend/src/app/globals.css`.

---

## Design Tokens

Defined in `:root` in `globals.css`. All Phase 1+ components must consume
tokens; no hardcoded hexes.

### Colors

| Token | Value | Usage |
|---|---|---|
| `--color-bg-body` | `#F7F7F7` | Dashboard / page background |
| `--color-text-body` | `#3C3C3C` | Default text |
| `--color-text-muted` | `#666766` | Grid column headers, secondary labels |
| `--color-brand-teal-dark` | `#112d32` | Login primary, top-bar chip bg, dark pills |
| `--color-brand-teal` | `#407874` | Secondary teal: Forgot Password link, Employee Login |
| `--color-brand-teal-mid` | `#00969F` | Sidebar gradient endpoint, focus rings |
| `--color-brand-green` | `#14AC36` | Primary CTAs: Submit Bids, Import, sidebar gradient start |
| `--color-carryover-tint` | `rgba(0, 150, 159, 0.18)` | Carryover button background (teal tint, not green) |
| `--color-carryover-tint-hover` | `rgba(0, 150, 159, 0.26)` | Carryover button hover state |
| `--color-text-inverse` | `#FFFFFF` | Text on brand-colored pill buttons (green, teal) |
| `--color-input-border` | `#D0D0D0` | Default input / outline-button border |
| `--color-warning-red` | `#C21E1E` | "Minimum starting bid" label (provisional — TODO eyedropper) |
| `--color-surface-cream` | `#EFEBE4` | Login card background — warm cream, distinct from `--color-bg-body` (`#F7F7F7`). Phase 1 addition. |
| `--color-text-on-dark-pill` | `#F0EBE3` | Login / Contact Us pill text — Mendix QA uses cream-off-white, not pure white. Phase 1 addition. |
| `--color-divider` | `#D1CDC6` | Horizontal rule between Employee Login and the footer section. Phase 1 addition. |
| `--color-input-border-dark` | `#898787` | Login input border — stronger contrast than `--color-input-border`. Phase 1 addition. |
| `--color-input-text` | `#333333` | Login input text — one step darker than `--color-text-body`. Phase 1 addition. |
| `--color-surface-white` | `#FFFFFF` | Pure-white surface background — login-card left panel behind hero photo. Phase 1 addition. |

### Shadows

| Token | Value | Usage |
|---|---|---|
| `--shadow-card` | `0 4px 15px rgba(0, 0, 0, 0.2)` | Login card + modal surface elevation. Phase 1 addition. |

### Typography

| Token | Value | Usage |
|---|---|---|
| `--font-family-primary` | `'Brandon Grotesque', 'Open Sans', Arial, sans-serif` | All text |
| `--font-size-body` | `16px` | Body copy |
| `--font-weight-body` | `400` | Body copy, large pill labels |
| `--font-size-button-lg` | `18px` | Submit Bids, Import, Login labels |
| `--font-size-button-sm` | `14px` | Export / Import outline buttons |
| `--font-weight-button-sm` | `500` | Small outline/carryover buttons (14px); large pill buttons use `--font-weight-body` (400) per QA |

### Radii

| Token | Value | Usage |
|---|---|---|
| `--radius-pill` | `44px` | Submit Bids, Login, Contact Us, buyer-code picker cards, Carryover |
| `--radius-input` | `4px` | Text inputs, filter search boxes, Export/Import outline buttons |

### Sidebar

| Token | Value |
|---|---|
| `--sidebar-gradient` | `linear-gradient(155.66deg, var(--color-brand-green) -12.99%, var(--color-brand-teal-mid) 83.48%)` |

Apply to the `.region-sidebar` container (not individual menu items).

---

## Button Primitives

Four global CSS classes. Apply via `className` in JSX — no import needed
(they live in the global stylesheet).

### `.btn-primary-green`

Commit/submit actions: **Submit Bids**, modal **Import**, modal **Close**.

```tsx
<button type="submit" className="btn-primary-green">Submit Bids</button>
```

| Property | Value |
|---|---|
| Background | `var(--color-brand-green)` `#14AC36` |
| Border | `1px solid var(--color-brand-green)` |
| Text | white, 18px / 400 |
| Radius | `var(--radius-pill)` 44px |
| Padding | `10px 28px` (matches 152×43 observed box) |

### `.btn-primary-teal`

Brand/navigation primaries: **Login**, **Contact Us**.

```tsx
<button type="submit" className="btn-primary-teal">Login</button>
```

| Property | Value |
|---|---|
| Background | `var(--color-brand-teal-dark)` `#112d32` |
| Border | `1px solid var(--color-brand-teal-dark)` |
| Text | white, 18px / 400 |
| Radius | `var(--radius-pill)` 44px |
| Padding | `10px 28px` |

### `.btn-outline`

Tertiary actions in the auction header: **Export**, **Import** (the small ones next to the round heading).

```tsx
<button type="button" className="btn-outline">↓ Export</button>
```

| Property | Value |
|---|---|
| Background | `var(--color-bg-body)` `#F7F7F7` |
| Border | `1px solid var(--color-input-border)` `#D0D0D0` |
| Text | `var(--color-text-body)`, 14px / `var(--font-weight-button-sm)` 500 |
| Radius | `var(--radius-input)` 4px |
| Padding | `7px 14px` |

### `.btn-carryover`

The **Carryover** pill in the sub-header row above the grid.

```tsx
<button type="button" className="btn-carryover">Carryover</button>
```

| Property | Value |
|---|---|
| Background | `var(--color-carryover-tint)` `rgba(0, 150, 159, 0.18)` |
| Border | none |
| Text | `var(--color-text-body)`, 14px / `var(--font-weight-button-sm)` 500 |
| Radius | `var(--radius-pill)` 44px |
| Padding | `7px 18px` |

---

## Shared behavior (all four variants)

All button classes share these rules, declared via a multi-selector block:

- `display: inline-flex; align-items: center; justify-content: center; gap: 6px`
- `cursor: pointer; white-space: nowrap`
- `font-family: var(--font-family-primary)`
- `transition: opacity 120ms ease, box-shadow 120ms ease`
- `:focus-visible` — `outline: 2px solid var(--color-brand-teal-mid); outline-offset: 2px`
- `:disabled` — `opacity: 0.5; cursor: not-allowed`

---

## Extending in CSS Modules

When a component needs to add layout-specific sizing on top of a primitive:

```css
/* bidder-dashboard.module.css */
.submitBidsBtn {
  /* Extends .btn-primary-green — add layout props only */
  width: 152px;
  height: 43px;
}
```

```tsx
import styles from './bidder-dashboard.module.css';

<button className={`btn-primary-green ${styles.submitBidsBtn}`}>Submit Bids</button>
```

Do not redeclare color, radius, or font in the module override — those come
from the global primitive.

---

## Font loading

Brandon Grotesque OTF files live at:
- `frontend/public/fonts/brandon-grotesque/Brandon_reg.otf` (weight 400)
- `frontend/public/fonts/brandon-grotesque/Brandon_med.otf` (weight 500)

Declared via `@font-face` in `globals.css` with `font-display: swap`.

To verify loading in the browser: DevTools → Network → filter by font →
look for `Brandon_reg.otf` and `Brandon_med.otf` on any page that renders
body text.

Additional weights (700 bold, 200 light, etc.) are available in
`migration_context/styling/` and can be promoted to `brandon-grotesque/`
when a Phase 1+ surface actually needs them. See the commented-out block
in `globals.css`.
