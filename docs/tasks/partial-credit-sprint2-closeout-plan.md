# Partial Credit — Sprint 2 Close-out Plan

> **Status:** Drafted 2026-05-11. Audits 4 Sprint-2 commits (`cc39885` → `0f3c543`) against `docs/tasks/partial-credit-modern-implementation-plan.md` §3 and the Confluence acceptance criteria.
> **Owner:** Sprint 2 finisher (next session)
> **Related:**
>  - `docs/tasks/partial-credit-modern-implementation-plan.md` (modern plan, source of truth)
>  - `docs/tasks/partial-credit-sprint2-design-notes.md` (Figma frame inventory + verbatim copy)
>  - `../docs/tasks/partial-credit-implementation-plan.md` §6, §7, §10 (Mendix-targeted engineering detail)
>  - `../docs/tasks/partial-credit-confluence.md` §"Acceptance criteria — Buyer submission"
>  - `../docs/tasks/partial-credit-jira-snapshot.md` (SPKB-3653 epic, SPKB-3654 wizard, SPKB-3657 validation)

This document is durable — Sprint 3 work should reference it, and any close-out commit should tick its boxes inline (do not rewrite the file from scratch).

---

## 1. Sprint 2 plan vs. delivered — checklist

Legend: ✅ done · ⚠️ partial / spec deviation · ❌ not started · ➖ deliberately deferred (see §3).

### 1.1 Modern plan §3 (Sprint 2 — Buyer wizard + validation + landing) bullets

| Promised (modern plan §3) | Status | Where it landed | Notes |
|---|---|---|---|
| Live Snowflake reader implementation (the stubbed methods from Sprint 1 #7) | ✅ | `cc39885` — `backend/.../service/partialcredit/snowflake/{CreditRequestSnowflakeReader,LoggingCreditRequestSnowflakeReader,JdbcCreditRequestSnowflakeReader}.java` | All 6 methods implemented; toggle `partial-credit.snowflake.reader=logging|jdbc`. Default is `logging` (correct for dev). |
| Submission validation service (`CreditRequestValidator`) | ⚠️ | `356aa07` — `CreditRequestValidator.java`, 21 unit tests | All 5 rules from §7 covered at submit time. **Gap:** 30-day window is only checked at submit; the wizard Step 1 "Next" does NOT re-validate (see §2.9). Order ownership IS re-checked at submit (defence-in-depth) but the wizard Step 1 already trusts the buyer's input — no live `validateOrderForBuyer` call from the frontend. |
| Order ownership rule | ✅ | `CreditRequestValidator.validateOrderEligibility` line 101 | Calls `snowflakeReader.validateOrderForBuyer`. |
| 30-day window rule | ⚠️ | `CreditRequestValidator.validateOrderEligibility` line 107-112 | Submit-time only — see §2.9. |
| No active duplicate rule | ✅ | `CreditRequestValidator.validateOrderEligibility` line 117-123 + `CreditRequestRepository.findActiveByOrderAndBuyer` | Excludes DECLINED + excludes the current draft from the conflict set. |
| ≥1 reason rule | ✅ | `CreditRequestValidator.validateReasonSelection` line 126 | |
| Barcode reconciliation | ⚠️ | `CreditRequestValidator.reconcileBarcodes` line 171 — implemented + tested, but **never called by any wizard page or endpoint** | The wizard persists raw pasted barcodes verbatim; the "Removed N duplicate and M not in order" banner from Figma `Missing Device - Barcode List` (line 37462) is never shown. See §2.2. |
| Damage Q&A rule | ✅ | `CreditRequestValidator.validateDamageAnswer` line 157 + `photoRepository.countByCreditRequestIdAndKind` | Submit-time check that YES requires ≥1 photo. **Note:** since photo upload is deferred to Sprint 4 (§3), the YES branch effectively blocks submit today — see §2.10. |
| `POST /api/v1/buyer/partial-credit/draft` | ✅ | `0a509ff` — `BuyerPartialCreditController.createDraft` | |
| `PATCH /api/v1/buyer/partial-credit/{id}` | ✅ | `BuyerPartialCreditController.update` | Accepts the 3 reason booleans + `shipmentDamaged`. |
| `POST .../{id}/submit` | ✅ | `BuyerPartialCreditController.submit` + `CreditRequestService.submit` | Runs validator → denormalises manifest fields → flips status to PENDING_APPROVAL. |
| `GET /api/v1/buyer/partial-credit?status=...` | ✅ | `BuyerPartialCreditController.list` | Status filter is in-memory (comment line 142-144); fine for buyer landing volumes. |
| `GET .../{id}` | ✅ | `BuyerPartialCreditController.getById` | Returns `CreditRequestDetail` with all 3 line collections. |
| `/wholesale/partial-credit` (landing) | ✅ | `0f3c543` — `frontend/.../partial-credit/page.tsx` | 6-column table; empty state copy correct; missing Figma details — see §2.6. |
| `/wholesale/partial-credit/new` (wizard step 1) | ✅ | `frontend/.../new/page.tsx` | |
| Wizard steps 2–5 as in-page state | ⚠️ | `frontend/.../new/{missing,wrong,encumbered,summary}/page.tsx` | Shipped as **separate routes** (not in-page state); deviation from the modern plan §3 wording but arguably better. Calls out: navigation is `router.push` between routes carrying `?id=` — refresh-safe and bookmark-safe. |
| Email-send stubs | ➖ | (not built) | Modern plan §3 explicitly: "Email-send stubs (deferred until Sprint 4)". Confirm intentional. See §3. |

### 1.2 Confluence §"Acceptance criteria — Buyer submission"

| Acceptance criterion | Status | Where it landed | Notes |
|---|---|---|---|
| Buyer can submit a single order, multi-reason request | ✅ | E2E happy path: `new/page.tsx` → missing → wrong → encumbered → summary → submit | Verified by reading flow; no Playwright test yet (§2.5). |
| At least one reason is enforced | ✅ | Frontend gate (`canNext = orderNumber && anyReason`) + backend (`ValidationIssue.noReasonSelected`) | Defence in depth. |
| Order # must belong to buyer and be within 30 days | ⚠️ | Both rules live in `CreditRequestValidator` (submit-time). The wizard Step 1 "Next" never calls `validateOrderForBuyer` or checks the shipped-date — buyer can fill in any string and proceed all the way to summary before the rule fires. | Spec deviation. Confluence §155 lists this as a Step 1 rule. See §2.9. |
| Buyer cannot have an active duplicate (unless previous fully Declined) | ✅ | `CreditRequestValidator` + `findActiveByOrderAndBuyer` | DECLINED rows excluded; current draft excluded from conflict set. |
| Barcode reconciliation banner appears with accurate dedup + not-in-order counts | ❌ | Code exists (`reconcileBarcodes`) but is NOT wired into any wizard page. Wizard persists all pasted barcodes; submit-time denormalisation silently drops barcodes the manifest does not know. | **Largest UX spec deviation.** See §2.2. |
| Damage Y/N is required; if Yes, photos are required | ⚠️ | Damage Y/N enforced (✅). Photo requirement is enforced server-side, but photo upload is Sprint 4 — so YES path can't pass validation today. | See §2.10. |
| Upload limits are enforced | ➖ | No upload UI exists yet; limits land with the upload feature in Sprint 4. | Confirm with product. |

---

## 2. Confirmed gaps with effort + ordering

### 2.1 Migration IT missing (Sprint 1 spillover)

- **Scope:** Sprint 1 plan Task 8 ("Integration test for migration via Testcontainers") and Task 9 (manual sql verification of seeded statuses) were never delivered with commit `4394c3c`. The V89 migration applies cleanly in dev (we have proof from the seeded ROLE rows resolving at startup), but there is no automated check.
- **File pointers:**
  - Migration: `backend/src/main/resources/db/migration/V89__partial_credit_init.sql` (single consolidated file — Sprint 1 plan called for V86 + V87 split; the split was collapsed).
  - Convention to mirror: `backend/src/test/java/com/ecoatm/salesplatform/partialcredit/PartialCreditMigrationIT.java` (does not exist — create).
  - Reference test: any of `auctions.reservebid.*MigrationIT.java` or `auctions.purchaseorder.*MigrationIT.java`.
- **Effort:** S (≤2h).
- **Order:** Earliest. Has no dependencies. Worth shipping before any other gap because every other gap relies on the schema being known-good.
- **Suggested test:** Boot a Testcontainers PG instance, run Flyway through V89, assert: (a) `partial_credit` schema exists, (b) the 5 `credit_request_statuses` rows are present in `sort_order` order, (c) the 4 `PartialCredit_*` rows are present in `identity.user_roles`.

### 2.2 Barcode reconciliation banner is not wired into the wizard

- **Scope:** `CreditRequestValidator.reconcileBarcodes` is fully implemented and unit-tested (8 of the 21 tests in `CreditRequestValidatorTest` cover it), but it has no caller anywhere. The wizard sends raw `barcodes: string[]` arrays via `POST /missing-lines`, `/wrong-lines`, `/encumbered-lines`; the controller persists them verbatim; `denormaliseLines` at submit silently sets line fields to null for unknown barcodes. The Figma banner copy `"Removed N duplicate and M not in order"` (frames at lines 37462, 21348, 39344) never renders.
- **File pointers:**
  - `backend/.../service/partialcredit/CreditRequestValidator.java#reconcileBarcodes` (call this).
  - `backend/.../service/partialcredit/CreditRequestService.java` lines 187-246 — these are the three `replaceXxxLines` methods that should run reconciliation.
  - `backend/.../controller/BuyerPartialCreditController.java` lines 91-113 — three line-set endpoints; response shape needs to grow a `reconciliation` block (banner + dropped barcodes).
  - `frontend/src/lib/partialCreditClient.ts` lines 163-189 — `setMissingLines`, `setWrongLines`, `setEncumberedLines`. Extend response shape and Zod schema.
  - `frontend/src/app/(dashboard)/wholesale/partial-credit/new/missing/MissingDevicesStep.tsx` lines 52-69 — the `onNext` handler is where the banner should land in state and render above the textarea.
  - Same for `wrong/WrongDevicesStep.tsx` (lines 63-81) and `encumbered/EncumberedDevicesStep.tsx` (lines 46-57).
- **Effort:** M (4-6h — backend response shape + DTO + Zod + 3 page renders + tests).
- **Order:** Right after 2.1. This is the single largest spec deviation in Sprint 2; closing it makes the wizard match Figma + Confluence.
- **Suggested test:**
  - Backend: integration test on `POST /missing-lines` with a body where 1 barcode is duplicated and 2 are not on the manifest; assert response banner string is exactly `"Removed 1 duplicate and 2 not in order."` and `validLines.length` equals the dedup'd-and-matched count.
  - Frontend: a unit / RTL test on `MissingDevicesStep` mocking `setMissingLines` to return a banner; assert the banner renders.

### 2.3 Order picker UX from Figma is missing

- **Scope:** Figma frame `Start Credit Request` (populated, line 735) shows the buyer entering an order number directly. The reader implements `listShippedOrdersForBuyer` (`CreditRequestSnowflakeReader.java` line 50) but no endpoint or frontend code calls it. The wizard requires a typed order number with no auto-complete / recent-orders dropdown.
- **File pointers:**
  - Reader method exists: `CreditRequestSnowflakeReader.listShippedOrdersForBuyer` returns `List<OrderSummary>` (record at line 84 of the interface).
  - No controller endpoint exists today. Would land as `GET /api/v1/buyer/partial-credit/shipped-orders?buyerCodeId=...`.
  - Frontend: `frontend/.../new/page.tsx` lines 80-90 — replace plain `<input>` with a typeahead / select.
- **Effort:** M (3-4h backend endpoint + DTO; M frontend — depends on whether we reuse an existing combobox component).
- **Order:** After 2.2. This is UX polish — the typed-input flow works end-to-end; the picker is the better experience but not load-bearing.
- **Open question:** see §5.1.
- **Suggested test:** Playwright E2E (§2.5) that asserts the dropdown shows N shipped orders for the test buyer.

### 2.4 Submit confirmation modal — compare against Figma

- **Scope:** Current modal (`SummaryStep.tsx` lines 155-188) renders `✓` icon, "Request submitted!" heading, a one-line subtitle, and a "Back to Credit Requests" button. Figma frame `Request Credit Confirmation` (line 9591) overlay (line 11097) shows:
  - 520px wide modal with `#F0F6EF` background and `#F7F5F1` 12px border ("halo" effect).
  - Modal scrim `rgba(28,27,28,0.5)`.
  - `circle-check` Font Awesome icon, 32px, `#14AC36`.
  - Heading "Request submitted!" in Founders Grotesk Medium 26.
  - **No buttons**; auto-dismiss / scrim-click closes per design note §2.5.5.
- **File pointers:**
  - `frontend/.../new/summary/SummaryStep.tsx` lines 155-188.
  - `frontend/.../wholesale/partial-credit/wizard.module.css` (`.confirmModal`, `.confirmCard` — add the halo border, the green panel fill, the 520px width).
- **Effort:** S (1-2h — pure CSS + dismiss-on-overlay-click). Consider keeping the "Back to Credit Requests" button despite Figma's missing CTA — pure auto-dismiss is hostile for screen-reader users; honour Figma but add a visually-hidden close affordance.
- **Order:** Independent. Any time.
- **Suggested test:** Visual regression via Playwright at 1440px; AA contrast check on the success heading.

### 2.5 No Playwright E2E for the wizard

- **Scope:** Project convention (`CLAUDE.md` §"Styling QA Verification" + every neighbouring feature) is that every buyer-facing flow has a Playwright spec under `frontend/tests/e2e/` or `frontend/tests/pws/`. Neighbouring features: `frontend/tests/e2e/admin-purchase-orders.spec.ts`, `frontend/tests/e2e/reserveBid.spec.ts`. No partial-credit spec exists.
- **File pointers:**
  - Create `frontend/tests/e2e/partial-credit-submit.spec.ts`.
  - Auth helper to reuse: `frontend/tests/e2e/wholesale-buyer-login.spec.ts` shows the bidder login + buyer-code-pick pattern.
- **Effort:** M (3-4h, including a happy path + one validation failure case).
- **Order:** Should run after 2.2 so the test asserts the reconciliation banner.
- **Suggested test paths:**
  - Happy path: login as `bidder@buyerco.com`, pick a buyer code, click `+ Submit a Credit Request`, fill the order #, tick all 3 reasons, paste 3 barcodes per reason, answer damage = No, click through to summary, click Submit, assert the confirmation modal appears and routes back to the landing with a new row.
  - Validation: pick no reasons, assert Next is disabled.
  - Note: in dev, the Snowflake reader is `logging` so the order-on-manifest check returns false; the test should hit `/submit` and assert the `ORDER_NOT_FOUND` issue surfaces (this also validates the wizard's error UX). Sprint 3 can swap to a stubbed reader for happy path.

### 2.6 Buyer landing page deviations from Figma

- **Scope:** Several Figma deviations on `Credit Requests Landing` (lines 12246-13544):
  - Figma columns are `Date Submitted` / `Order Number` / `Request Reasons` / `Status` + action eye icon. Code has 6 columns including `Request #`, `Devices`, `Submitted` — extra columns are not in Figma.
  - Figma "Credit Request Policy" link is missing from the heading row.
  - Figma landing banner copy (`landingBanner` in design notes line 855) is missing — including the R-2-not-certified RMA copy. This is the **only** place where the R-2 copy appears.
  - Filter chips (`Ab` icon + `calendar-days` icon) are missing — there is no client-side filter UI for date / order / reasons / status.
  - Empty-state copy is "You have not submitted any partial credit requests yet" (line 61) vs. Figma's "There are currently no Partial Credit Requests" (verbatim — landing-empty).
  - Status pill colours match Figma roughly (orange/green/red) — fine.
- **File pointers:**
  - `frontend/.../wholesale/partial-credit/page.tsx` lines 37-99 (everything).
  - `frontend/.../wholesale/partial-credit/wizard.module.css` lines 256-259 (status pill colours — present).
- **Effort:** M (3-4h — column trim + banner copy + Credit Request Policy link + filter chips).
- **Order:** After 2.2 so the banner can coexist with the rest of the polish.
- **Suggested test:** Use the existing Playwright styling-QA pattern (CLAUDE.md §"Styling QA Verification") — screenshot local landing vs. QA `https://buy-qa.ecoatmdirect.com/p/login/web` for visual parity. (The QA env does not have the modern partial-credit page; instead compare against the Figma frame.)

### 2.7 No buyer detail view (post-submit read-only page)

- **Scope:** Landing rows have `onClick={() => router.push(`/wholesale/partial-credit/${r.id}`)}` (`page.tsx` line 83) but **no route exists at that path**. Clicking a row will 404. Figma frames `Credit Request Detail Page` (lines 13545-18978) define the read-only post-submit view (header strip + 3 reason tables).
- **File pointers:**
  - Need to create `frontend/src/app/(dashboard)/wholesale/partial-credit/[id]/page.tsx`.
  - Backend endpoint `GET /{id}` already exists and returns the full `CreditRequestDetail` — no backend work needed.
- **Effort:** M (4h — read-only views for 3 line tables + header strip). The modern plan §3 places `CreditRequest_BuyerDetail` under Sprint 4 (`SPKB-3669`), so this is **arguably deferable** — but the landing's `onClick` will 404 until then. Either build a stub detail view this sprint OR remove the row `onClick` so the row isn't a broken link.
- **Order:** Either close as a stub now (lower-cost defensive fix) or remove the click handler. Recommendation: remove the click handler for Sprint 2, ship the full detail view with Sprint 4 / SPKB-3669.
- **Suggested test:** Once built, Playwright assertion: submit a request, click the row, assert the request number heading is present.

### 2.8 Sprint 4 placeholder lookups (TODO inventory)

Found via `grep -i "sprint 4\|todo\|placeholder\|deferred"`:

- `backend/.../service/partialcredit/CreditRequestService.java` line 349 — request number generator uses `System.currentTimeMillis() % 1_000_000L`; comment says "Sprint 4 can replace with a proper sequence". **Risk:** duplicate request numbers if two drafts created in the same millisecond. Low probability, but a real correctness gap.
- `frontend/.../new/wrong/WrongDevicesStep.tsx` line 25 — "Photo uploads + the photo modal are Sprint 4 scope." Sprint 4 deferral consistent with modern plan §3. ✅ deliberate.
- `frontend/.../new/missing/MissingDevicesStep.tsx` line 21 + line 130 — "Photo uploads are Sprint 4 scope" + the YES-path helper text "Photo uploads will be available in a future update. You can still proceed." **But** the backend validator at `CreditRequestValidator.java` line 163-167 rejects submit when damage=YES and no damage photos exist. So if a buyer answers YES today, they cannot submit. See §2.10.
- `frontend/.../StepIndicator.tsx` line 23 — "collapses to a single 'Device Details' placeholder" — matches Figma initial frame. ✅ deliberate.

**Effort:** Document the request-number risk in `docs/tasks/partial-credit-modern-implementation-plan.md` Sprint 4 section; ticket it for SPKB-3669 follow-up. S.

### 2.9 30-day-window rule does not fire before submit

- **Scope:** `CreditRequestValidator.validateOrderEligibility` lines 105-112 only check the window when called from `submit()`. Wizard Step 1 never re-checks. The Confluence rule (line 154) and the modern plan §7 (`VAL_OrderWithinCreditWindow` triggered on "Step 1 Next") both place this on Step 1. Current behaviour: buyer can fill an old order, complete all 5 steps, click Submit, then get the error → bad UX.
- **File pointers:**
  - `backend/.../service/partialcredit/CreditRequestService.java#createDraft` lines 87-117 — should fail-fast on the window when the order header is available.
  - Alternative: introduce a `POST /api/v1/buyer/partial-credit/validate-order` pre-check endpoint that the wizard Step 1 "Next" calls before the draft is created.
- **Effort:** S (1-2h).
- **Order:** Independent. Bundle with 2.10 — both surface validator rules earlier.
- **Suggested test:** Service unit test on `createDraft` with a stub Snowflake reader returning an `OrderHeader` shipped 31 days ago — assert it throws / returns a typed error.

### 2.10 Damage=YES path is unusable today (validator blocks submit)

- **Scope:** `CreditRequestValidator.validateDamageAnswer` line 163-167 requires ≥1 `DAMAGE` photo in `credit_request_photos` when `shipmentDamaged = YES`. The wizard does not upload photos (deferred to Sprint 4). Therefore: any buyer who answers YES gets `DAMAGE_REQUIRES_PHOTO` at submit and cannot proceed.
- **File pointers:**
  - `backend/.../service/partialcredit/CreditRequestValidator.java` lines 157-169.
  - `frontend/.../new/missing/MissingDevicesStep.tsx` line 130 (the misleading "You can still proceed" copy).
- **Options:**
  1. Disable the photo check until Sprint 4 lands the upload UI; remove the misleading copy.
  2. Build a minimal photo upload UI now (≠ Sprint 4 scope per modern plan §3).
  3. Hide the YES radio until Sprint 4 (simplest; tightest scope discipline).
- **Effort:** S — option 1 or 3.
- **Order:** Bundle with 2.9.
- **Recommendation:** Option 1 — gate the photo check behind a feature flag (`partial-credit.damage-photo-required: true|false`, default `false` until Sprint 4 ships uploads). Keeps the rule in code for Sprint 4 enablement.
- **Suggested test:** Validator unit test asserting the rule fires when the flag is on; passes when the flag is off.

### 2.11 No file-drop / xlsx-csv-docx parsing for barcodes

- **Scope:** Figma every barcode step has an `OR` divider + dropzone (`Click or drag and drop file here to upload`, `Accepted formats: .xlsx, .csv, .docx`). The wizard ships textarea-only.
- **File pointers:**
  - The Mendix-targeted plan (`../docs/tasks/partial-credit-implementation-plan.md` §16, line 451-453) **includes** file upload in Sprint 2 scope.
  - The modern plan §3 omits it, implicitly deferring. Confluence acceptance criteria §"Upload limits are enforced" implies it must ship eventually but is silent on which sprint.
- **Effort:** L (6-8h — file parsing + storage + UI + validator rules).
- **Order:** Recommend defer to Sprint 4 alongside photo upload (similar mechanical surface — file input, S3-or-bytea storage, validation rules).
- **Open question:** see §5.2.

---

## 3. What's deliberately deferred (Sprint 3 / 4 boundary)

These are NOT close-out items — they belong to later sprints by design. Listed for boundary clarity:

| Item | Deferred to | Source |
|---|---|---|
| Photo uploads (damage + wrong-device) | Sprint 4 | modern plan §3 (line 68) + design-notes §2.10 |
| Email: "Request Submitted" to buyer + sales-ops | Sprint 4 | modern plan §3 ("Email-send stubs (deferred until Sprint 4)") |
| Sales-rep on-behalf submission | Sprint 4 / SPKB-3659 | modern plan §3 + §6 |
| Buyer detail page `CreditRequest_BuyerDetail` | Sprint 4 / SPKB-3669 | modern plan §3 line 78 — **caveat:** the landing row `onClick` 404s until then; see §2.7 for the short-term mitigation. |
| Excel export | Sprint 4 | modern plan §3 line 78 |
| Admin landing + review pages | Sprint 3 | modern plan §3 (Sprint 3 section, lines 70-76) |
| Credit calculation engine + recommendation engine | Sprint 3 | modern plan §3 + impl-plan §8/§9 |
| Bulk Accept / Decline + Complete Review | Sprint 3 | modern plan §3 |
| Ship Status column (Missing tab in admin) | Sprint 3 / SPKB-3658 | impl-plan §16 line 464 — but the `getShipStatusForBarcode` reader method shipped in `cc39885` so the data is wired and ready |
| Permissions role mapping (Bidder → PartialCredit_Buyer etc.) | Sprint 4 / SPKB-3659 | modern plan §6 |
| Post-submit photo upload on Wrong Device lines | Sprint 4 / SPKB-3662 | modern plan §3 + decision log #4 |
| R-2 cert auto-block in wizard Step 1 | Phase 2 | modern plan §7 (Out-of-scope) |

---

## 4. Recommended close-out sequence

Five chunks, each a bisectable single commit. Total ≈ 14-18 hours.

### Chunk 1 — Schema confidence (S, ~2h)
- Build `PartialCreditMigrationIT` (§2.1).
- Commit: `test(partial-credit): migration IT for V89 schema + status seed`.
- **Why first:** unblocks every subsequent chunk by guaranteeing the schema applies cleanly. Costless if green, surfaces the V86+V87→V89 collapse if anything is wrong.

### Chunk 2 — Barcode reconciliation banner (M, ~4-6h)
- Wire `CreditRequestValidator.reconcileBarcodes` into `replaceMissingLines / replaceWrongLines / replaceEncumberedLines` (§2.2).
- Extend the controller response shape with `{ banner, dropped }`.
- Update Zod schema + 3 wizard pages to render the banner.
- Commit: `feat(partial-credit): wire barcode reconciliation banner into wizard`.
- **Why second:** closes the largest spec deviation. Sets up Chunk 3's E2E test to assert the banner.

### Chunk 3 — Validator surface earlier + damage gate (S, ~2-3h)
- Move 30-day window check + order-belongs-to-buyer check into draft creation (§2.9).
- Feature-flag the damage-photo requirement so YES doesn't soft-brick the wizard (§2.10).
- Commit: `fix(partial-credit): surface order eligibility + window rule on Step 1; gate damage photo requirement`.

### Chunk 4 — Landing + confirmation polish (M, ~4-6h)
- Trim landing columns to Figma's 4 + action icon (§2.6).
- Add Credit Request Policy link + landing banner copy (including R-2 RMA copy) (§2.6).
- Remove the broken row `onClick` until the Sprint 4 detail view ships (§2.7).
- Match the confirmation modal to Figma — halo border, 520px width, scrim colour, auto-dismiss with a visible close affordance for a11y (§2.4).
- Commit: `feat(partial-credit): landing copy + columns + submit modal pixel parity`.

### Chunk 5 — Playwright E2E (M, ~3-4h)
- `frontend/tests/e2e/partial-credit-submit.spec.ts` (§2.5):
  - Happy path through all 5 steps with all 3 reasons.
  - Validation: empty reasons → Next disabled.
  - Reconciliation: pasted blob with duplicates + a non-manifest barcode → banner copy matches.
- Commit: `test(partial-credit): e2e wizard happy path + reconciliation banner`.
- **Why last:** all behavioural changes from Chunks 2-4 are in place; the spec asserts the final shape.

After Chunk 5, Sprint 2 is genuinely done.

---

## 5. Open questions — RESOLVED 2026-05-11

### 5.1 Order picker: Snowflake dropdown vs. typed-only? — **TYPED + STEP-1 VALIDATE**

- Keep typed input. Add a validate-order pre-check on Step 1 "Next" that calls `validateOrderForBuyer` + the 30-day window check, surfacing failures inline before draft creation.
- Dropdown deferred to Sprint 3 enhancement.
- → folded into Chunk 3.

### 5.2 File-drop / xlsx-csv-docx parsing — **DEFER TO SPRINT 4**

- Bundle with photo upload (same file-input + storage + validation surface).
- Document the decision in modern plan §3 so it has one canonical home.

### 5.3 Damage=YES treatment until Sprint 4 ships photos — **LET IT BREAK**

- No feature flag, no copy fix. The wizard remains soft-bricked on the YES path until Sprint 4 ships the photo upload UI.
- **Rationale:** active development phase, no production users; the friction is on the development team, not real buyers.
- → Chunk 3 drops the damage-flag work; Chunk 3 is now Step-1 pre-validate only.

### 5.4 Buyer detail page — **REMOVE THE ONCLICK**

- Drop the `router.push` on landing rows in `page.tsx` line 83. No detail page in Sprint 2.
- Full detail view ships with Sprint 4 / SPKB-3669.
- → folded into Chunk 4.

### 5.5 Email "Request Submitted" — **DEFER TO SPRINT 4** (confirmed)

- Modern plan §3 line 68 stands. No submit-email work in Sprint 2 close-out.

### 5.6 Request-number generator risk — **DEFER TO SPRINT 4** (confirmed)

- Acceptable as-is for Sprint 2 close-out. Sprint 4 replaces with a Postgres sequence + tightens the unique constraint.

---

## 6. Revised close-out sequence (post-decisions)

After locking the §5 answers, the chunk shape is:

| # | Chunk | Effort | Notes |
|---|---|---|---|
| 1 | Migration IT (§2.1) | S ~2h | Unchanged. |
| 2 | Wire reconciliation banner (§2.2) | M ~4-6h | Unchanged. |
| 3 | Step-1 pre-validate (window + order ownership) (§2.9) | S ~1-2h | Damage-flag work dropped per §5.3. |
| 4 | Landing polish + remove broken onClick + modal pixel parity (§2.4, §2.6, §2.7) | M ~4-6h | Unchanged. |
| 5 | Playwright E2E (§2.5) | M ~3-4h | Unchanged. |

Total: **~14-20 hours**. Five bisectable commits.

Damage=YES path remains broken intentionally. Buyer detail page remains absent intentionally. Order picker dropdown deferred to Sprint 3.
