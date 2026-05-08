# QA-vs-Local Page Audit — Reusable Playbook

A repeatable process for auditing any page in SalesPlatform_Modern against
its QA equivalent and producing two durable artifacts: a **functional
walkthrough** (UX/feature gaps) and a **pixel-parity styling spec** (CSS
tokens). Modeled on the 2026-05-08 Purchase Order audit which produced
three commits totaling ~800 lines of structured findings.

---

## When to use this

- User flags a "huge difference" or unspecified UX gap on a specific page
- New feature work needs to verify QA-parity before / after merge
- A Mendix → modern port of a page is "shipped" but feels incomplete
- A regression was reported and you need a structured comparison
- Onboarding: someone needs a fast, opinionated tour of one surface

**When NOT to use:**

- Cross-cutting audits across many pages (use the [phase-by-phase walkthrough
  pattern](qa-vs-local-pixel-walkthrough-2026-05-07.md) instead — broader,
  shallower per page)
- A pure visual regression check (use Playwright screenshot diffing, not this)
- A backend-only diff (this playbook is UX-first)

---

## Time budget

| Phase | Hours |
|---|---|
| Plan + scope + login | 0.25 |
| Functional walk on QA | 0.5–1 |
| Functional walk on local | 0.25–0.5 |
| Styling extraction (both apps) | 0.25 |
| Walkthrough doc | 0.5–1 |
| Styling spec doc | 0.5 |
| Commit + handoff | 0.25 |
| **Total** | **~2.5–3.5 hours per page** |

Don't try to audit more than 2 pages per session — context bloat kills the
quality of the writeup.

---

## Prerequisites

- [ ] Both apps reachable: QA at `https://buy-qa.ecoatmdirect.com`, local at `http://localhost:3000`
- [ ] Backend running (`mvn spring-boot:run` from `backend/`)
- [ ] Frontend running (`npm run dev` from `frontend/`)
- [ ] Admin creds for both: QA `ashirwadmittal / Password123#` (admin), local `admin@test.com / Admin123!`. Per-surface buyer creds in [CLAUDE.md](../../CLAUDE.md).
- [ ] Playwright MCP available (`mcp__playwright__browser_*` tools)
- [ ] Screenshot output directory exists or is created at the start
- [ ] You have read the existing [QA-vs-local walkthrough](qa-vs-local-pixel-walkthrough-2026-05-07.md) for context on what's already known

---

## The 6 phases

### Phase 1 — Scope

1. Identify the target page (e.g. "Purchase Order")
2. Create a screenshot subdirectory: `docs/tasks/qa-vs-local-2026-05-07/<page-slug>/`
3. Decide deliverable filenames (date-stamped): 
   - `qa-vs-local-<page-slug>-walkthrough-YYYY-MM-DD.md`
   - `qa-vs-local-<page-slug>-styling-spec-YYYY-MM-DD.md`
4. Use `TaskCreate` to track each phase as a separate task

### Phase 2 — Functional walk on QA

1. Login to QA admin via `mcp__playwright__browser_navigate("https://buy-qa.ecoatmdirect.com/login.html")` then fill + submit
   - **Pitfall:** QA session expires after ~30 min of inactivity. Plan to re-login mid-walk.
2. Navigate to the target page via the sidebar nav
3. Take a baseline full-page screenshot: `qa-01-list.png` (or whatever the landing state is)
4. **Click every interactive element systematically.** Suggested ordering:
   - Top bar buttons (Export / Refresh / Import / etc.)
   - Header selectors / dropdowns (week pickers, status filters)
   - Each column's sort header (toggle ascending / descending)
   - Each column's filter input (try one numeric + one text — the comparator menus differ)
   - Column visibility / settings icons (eye, gear)
   - Row-level actions (click a row, hover for inline buttons, right-click)
   - Any modals triggered by the above (Create / Edit / Delete confirmation)
   - Pagination (First / Prev / Next / Last)
5. **One screenshot per meaningful state.** Naming: `qa-NN-<short-action>.png` (zero-padded sequence). Examples: `qa-02-week-dropdown-open.png`, `qa-04-grade-comparator.png`.
6. While clicking, **take notes inline** about what each surface IS (modal? page? popover?) and any non-obvious behavior. The screenshot alone won't tell you that "the small ▼ next to the X opens a different menu than the large ▼".
7. **Pitfall — selectors:** Mendix DataGrid 2 uses `downshift-:rN:-toggle-button` ids that change per render. Don't pin to ids; use stable text or position-based selectors:
   ```js
   const btn = Array.from(document.querySelectorAll('button')).find(b => b.textContent?.trim() === 'Export');
   ```

### Phase 3 — Functional walk on local

1. Switch to a second browser tab on the local app (not the same tab — keep QA reachable)
   - **Pitfall:** the local cookie is `HttpOnly + SameSite=Strict` and shared across tabs. Logging in as bidder in tab B nukes admin in tab A. Plan re-logins between role switches.
2. Navigate to the equivalent local page
3. Mirror the QA click sequence as closely as possible
4. Naming: `local-NN-<short-action>.png` matching the QA file numbers where the action exists
5. When local is **missing** a feature entirely (modal, button, page), **don't fake a screenshot** — note the absence in the writeup with "(no equivalent)"

### Phase 4 — Styling extraction (both apps)

This is what makes the audit pixel-accurate instead of eyeball-accurate.
Run this `browser_evaluate` against QA, then again against local, and
diff the values. **Copy-paste the script verbatim** — adjust only the
selectors at the top to match the page you're auditing.

```js
() => {
  const cs = (el, ...props) => {
    if (!el) return null;
    const c = getComputedStyle(el);
    return Object.fromEntries(props.map(p => [p, c.getPropertyValue(p)]));
  };
  const COLORS = ['color', 'background-color', 'border-color', 'border-top-color', 'border-bottom-color'];
  const BOX = ['width', 'height', 'padding', 'margin', 'border', 'border-radius', 'box-shadow'];
  const TYPE = ['font-family', 'font-size', 'font-weight', 'line-height', 'letter-spacing', 'text-transform'];

  const all = {};
  // 1. Body / page-level baseline
  all.body = cs(document.body, 'background-color', 'color', 'font-family', 'font-size');

  // 2. Page heading — adjust the .find() match to your page title
  const heading = Array.from(document.querySelectorAll('h1,h2,h3'))
                       .find(h => h.textContent?.trim() === 'Purchase Order');
  all.heading = heading ? { tag: heading.tagName, ...cs(heading, ...COLORS, ...TYPE) } : 'not-found';

  // 3. Top action buttons — one block per button label
  const exportBtn = Array.from(document.querySelectorAll('button')).find(b => b.textContent?.trim() === 'Export');
  all.exportBtn = exportBtn ? cs(exportBtn, ...COLORS, ...BOX, ...TYPE) : 'not-found';

  // 4. Grid container, header cell, body cell
  const grid = document.querySelector('[role="grid"]') || document.querySelector('table');
  all.grid_container = grid ? cs(grid, 'background-color', 'border', 'box-shadow') : 'not-found';
  const headerCell = document.querySelector('[role="columnheader"]') || document.querySelector('th');
  all.header_cell = headerCell ? cs(headerCell, ...COLORS, ...BOX, ...TYPE) : 'not-found';
  const gridCell = document.querySelector('[role="gridcell"]') || document.querySelector('tbody td');
  all.grid_cell = gridCell ? cs(gridCell, ...COLORS, ...BOX, ...TYPE) : 'not-found';

  // 5. Filter input + comparator combobox
  const filterInput = document.querySelector('th input[type="text"], [role="columnheader"] input[type="text"]');
  all.filter_input = filterInput ? cs(filterInput, ...COLORS, ...BOX, ...TYPE) : 'not-found';
  const comparator = document.querySelector('button[role="combobox"]');
  all.comparator_btn = comparator ? cs(comparator, ...COLORS, ...BOX, ...TYPE) : 'not-found';

  // 6. Pagination buttons
  const pagBtn = document.querySelector('button[aria-label*="page" i]');
  all.pagination_btn = pagBtn ? cs(pagBtn, ...COLORS, ...BOX) : 'not-found';

  return all;
}
```

**Tips:**
- The comparator dropdown menu, modals, and tooltips often need their own
  trigger sequence before the element exists in the DOM. Open the menu
  first, *then* run the evaluate script.
- Pseudo-classes (`:hover`, `:focus`) won't appear in `getComputedStyle`
  unless you actually hover. Skip these — eyeball them from screenshots
  and note as "estimated" in the spec.
- Hex codes are easier to read than `rgb(N, N, N)`. Convert in the spec
  doc, but keep the rgb in raw evaluate output for evidence.

### Phase 5 — Write the walkthrough doc

Use the [PO walkthrough](qa-vs-local-po-walkthrough-2026-05-08.md) as the
template. Required sections:

1. **Conclusion up front** (1-2 sentences) — the lede. If the user asked
   about "huge UX difference," put the answer here, not buried.
2. **Page architecture** — most important section. Are QA and local
   showing the same data model? Different mental models drive every
   downstream delta and need to be flagged at the top.
3. **Per-section side-by-side tables** — one table per logical area
   (top bar, grid, modal, etc.). Three columns: Element / QA / Local.
   Include screenshot references inline.
4. **Severity-tagged gap inventory** — numbered IDs (e.g. PO-1...PO-13)
   with CRITICAL / HIGH / MEDIUM / LOW labels per the rubric below.
5. **Implementation plan** — sprint-sized chunks with effort estimates.
   If the gap is feature-class (not polish), say so explicitly.
6. **Screenshots index** — table mapping every screenshot to what it
   shows.

**Severity rubric** (consistent across audits):
- **CRITICAL** — local cannot perform the action that QA can, or feature
  is a dev scaffold; user-data integrity at risk
- **HIGH** — local action exists but materially different; missing
  control affects daily SalesOps workflow
- **MEDIUM** — visible workflow / copy delta likely to confuse a QA-
  trained user
- **LOW** — cosmetic only (icon, color, spacing)

### Phase 6 — Write the styling spec doc

Use the [PO styling spec](qa-vs-local-po-styling-spec-2026-05-08.md) as the
template. Required sections:

1. **Design tokens** — single CSS custom-properties block at the top.
   Group by purpose (color / typography / spacing / radius). Note where
   the spec conflicts with `CLAUDE.md` brand tokens — explicit decision.
2. **Per-component styling tables** — Property / QA actual / Local
   current / Spec to apply. Numbers from the evaluate script, not
   eyeballed.
3. **Inline CSS module skeletons** — actual `.poGrid { ... }` blocks
   readers can paste into a `.module.css`. Saves time on the rebuild.
4. **Local-only side fixes** — bugs visible on the existing local page
   that don't require the rebuild. Quick wins to ship today.
5. **Implementation checklist** — ordered steps so the rebuild can
   start small (tokens) and build up (component by component).
6. **Intentional divergences** — what we keep different, with
   ADR cross-references. Prevents the next audit from re-flagging
   already-decided drift.

### Phase 7 — Commit + handoff

- One commit per doc (or one bundle commit). Conventional commit format
  per `CLAUDE.md`. Detailed body explaining what was found, not just
  what was changed.
- Stage only the doc(s) + the screenshot subdirectory. Don't pick up
  unrelated untracked files.
- Surface the new doc in the next user-facing summary. Cross-link from
  the master `qa-vs-local-pixel-walkthrough-2026-05-07.md` index doc if
  this is a per-page deep-dive.

---

## Pitfalls catalog (from the PO audit)

| Pitfall | Mitigation |
|---|---|
| QA admin session expires mid-walk | Re-login when navigation lands on `/p/login/web` (the buyer login). Use `https://buy-qa.ecoatmdirect.com/login.html` for admin. |
| Local cookie shared across tabs | Plan re-logins between admin and bidder roles. Don't try to keep both alive. |
| Mendix DataGrid 2 uses dynamic `downshift-:rN:-toggle-button` ids | Select by stable text content via `Array.from(document.querySelectorAll('button')).find(b => b.textContent === '...')` |
| Comparator dropdown options exist in the DOM only when open | Trigger the dropdown first, then run the evaluate script |
| Postgres `::bigint` cast collides with Hibernate's named-param tokenizer | Use ANSI `CAST(... AS bigint)` — same workaround documented on `BidDataCreationRepository.CTE_SQL` (relevant if the audit touches backend) |
| Worktree drift if running parallel agents | Agents merge `main` into their worktree before starting (per [agent-1 + agent-2 reports](qa-vs-local-implementation-plan-2026-05-07.md)) |
| Screenshots saved to `.playwright-mcp/` not the docs folder | Always pass `filename: docs/tasks/<dir>/<file>.png` explicitly to `browser_take_screenshot` |
| Local edit/scaffold pages may reveal raw FK ids ("Week from id 557") | Flag as a HIGH data-resolution gap; don't write it off as just an eyesore |
| `+ New` style buttons that route to dev-scaffold pages | Walk through to the destination — don't trust that a list-page CTA leads to a real form |

---

## Anti-pattern: skipping the evaluate script

If you write a styling section based on screenshots only, **you will be wrong by 1-3px on every padding / font-size / line-height value.** The 2026-05-08 PO audit found:

- Heading was 42px, not the 32-36px the screenshot suggested
- Filter input was 30px tall (not 24-28px)
- Comparator button was exactly 37.25px wide
- All buttons used `padding: 10.8px 18px` (the .8 is non-obvious from a screenshot)
- Font-family on the comparator was `datagrid-filters` (an icon font!) — invisible from a screenshot

These exact numbers are what makes the styling spec actionable. Skip the
extraction, ship a "looks roughly QA" rebuild that the next audit will
flag as "still doesn't match."

---

## Templates (copy-paste these to start)

### Walkthrough doc skeleton

```markdown
# <Page Name> — QA vs Local Walkthrough (YYYY-MM-DD)

**Trigger:** <user request or context>

**Conclusion up front:** <1-2 sentence lede>

---

## 1. Page architecture
| Concept | QA | Local |
|---|---|---|
| ... | ... | ... |

## 2. Top bar / page header
### QA
- ...
### Local
- ...
### Severity
| Item | Severity | Notes |

## 3. <Major component, e.g. data grid>
...

## N. New gap inventory
**Critical:**
- **<PAGE>-1** <description>
**High:**
- **<PAGE>-2** ...

## N+1. Suggested implementation plan
### Sprint A — ...
### Sprint B — ...

## N+2. Screenshots index
| File | What it shows |
```

### Styling spec doc skeleton

```markdown
# <Page Name> — Pixel-Parity Styling Spec (YYYY-MM-DD)

**Scope:** ...
**Method:** All QA values pulled from getComputedStyle() against the live QA <page>.

## 1. Design tokens
```css
--<page>-bg:           #...;
--<page>-text:         #...;
--<page>-border:       #...;
--<page>-accent:       #...;
--<page>-font:         "...", sans-serif;
```

## 2. Page-level layout
| Element | QA | Local current | Spec |

## 3. Page heading
| Property | QA | Local | Spec |

## 4. <Component>
| Property | QA | Spec |
```css
.<component> {
  ...
}
```

## N. Local-only side fixes
| # | Bug | Fix |

## N+1. Implementation checklist
- [ ] Tokens
- [ ] Heading
- [ ] ...

## N+2. Intentional divergences
| Item | QA | Local | Decision |
```

---

## Reference

- Exemplar — Functional walkthrough: [`qa-vs-local-po-walkthrough-2026-05-08.md`](qa-vs-local-po-walkthrough-2026-05-08.md)
- Exemplar — Styling spec: [`qa-vs-local-po-styling-spec-2026-05-08.md`](qa-vs-local-po-styling-spec-2026-05-08.md)
- Master gap walkthrough (cross-page): [`qa-vs-local-pixel-walkthrough-2026-05-07.md`](qa-vs-local-pixel-walkthrough-2026-05-07.md)
- Master implementation plan: [`qa-vs-local-implementation-plan-2026-05-07.md`](qa-vs-local-implementation-plan-2026-05-07.md)
- Severity rubric (origin): same master walkthrough §1
- ADR log (for "intentional divergences"): [`docs/architecture/decisions.md`](../architecture/decisions.md)
