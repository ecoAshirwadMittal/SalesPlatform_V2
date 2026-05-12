# Partial Credit Requests — full flow

Phase 1 complete as of Sprint 4 (2026-05-12) — SPKB-3653 epic with stories
SPKB-3658, SPKB-3659, SPKB-3660, SPKB-3661, SPKB-3662, SPKB-3663,
SPKB-3664, SPKB-3669.

## Glossary

| Term | Meaning |
|---|---|
| **Credit Request** | Buyer-initiated claim that one or more devices in a shipped order were missing, wrong, or encumbered. Drives a credit issued back to the buyer's account. |
| **Reason** | Each request carries one or more of three reasons: `MISSING`, `WRONG`, `ENCUMBERED`. The line table per reason has slightly different columns. |
| **Line** | One row per barcode the buyer claims under a reason. Lines are reviewed individually by sales ops. |
| **Reconciliation** | Pre-submit step that verifies the pasted barcodes match the order's Snowflake manifest. Drops duplicates + non-manifest barcodes with a banner. |

## Lifecycle

```
            ┌──────────┐
   buyer    │  DRAFT   │  CreditRequestService.createDraft
   wizard ─►│          │─►  PATCH for reasons + ship-damaged Q&A
            │          │─►  setMissing/Wrong/Encumberedlines per reason
            └────┬─────┘
                 │ submit()
                 ▼
         ┌─────────────────┐
         │ PENDING_APPROVAL│  visible on admin landing
         └────────┬────────┘
                  │ POST /open-for-review
                  ▼
         ┌─────────────────┐
         │  UNDER_REVIEW   │  admin sets per-line / per-section /
         │                 │  global decisions; encumbered fields
         └────────┬────────┘
                  │ POST /complete-review
        ┌─────────┴─────────┐
        ▼                   ▼
  ┌──────────┐         ┌──────────┐
  │ APPROVED │         │ DECLINED │  email_audit row written,
  │          │         │          │  ReviewCompletedEvent fired
  └──────────┘         └──────────┘
                  │
                  └─► ReviewCompletedEmailListener (async, AFTER_COMMIT)
                       └─► EmailTemplateService.render(...)
                            └─► EmailSender + EmailAuditService.recordBatch(...)
```

Once final (`APPROVED` / `DECLINED`):
- Photos can no longer be uploaded or deleted (audit trail freezes).
- Buyer detail page reveals per-line decisions for the first time.
- `email_audit` records one row per recipient per send attempt.

## Buyer surface

`/wholesale/partial-credit/...` — gated by
`hasAnyRole('Bidder','SalesRep','Administrator')`.

| Route | Purpose |
|---|---|
| `/wholesale/partial-credit` | Landing — list of buyer's own requests, status pill from `credit_request_statuses` |
| `/wholesale/partial-credit/new` | Wizard step 1 — order number + reason flags. Reads `?draftId=X` for on-behalf resume |
| `/wholesale/partial-credit/new/missing` | Step 2 — barcode entry with **file-drop** (xlsx/csv/docx) hybrid + reconciliation |
| `/wholesale/partial-credit/new/wrong` | Step 3 — expected/actual device pairs |
| `/wholesale/partial-credit/new/encumbered` | Step 4 — barcodes only; admin fills Prolog Result + Actual Value |
| `/wholesale/partial-credit/new/summary` | Step 5 — review + submit |
| `/wholesale/partial-credit/[id]` | Read-only detail. Photo upload + gallery; reviewer summary post-finalisation |

Photo upload (POST `/photos`): 5 MB max, 5 photos per wrong-device line max,
image/* allowlist (jpeg/png/heic/webp). Blocked when status is final.
Photos uploaded with no `wrongDeviceLineId` are stored as request-scoped
`DAMAGE` kind; per-line uploads are `WRONG_DEVICE`.

## Admin surface

`/admin/auctions-data-center/partial-credit/...` — gated by
`hasAnyRole('SalesOps','SalesRep','Administrator','CoAdministrator')`.

| Route | Purpose |
|---|---|
| `/admin/.../partial-credit` | Landing — filterable list + status counter chips + Download xlsx |
| `/admin/.../partial-credit/[id]` | Review detail — 3 reason sections, per-line + bulk decisions, Complete Review modal |
| `/admin/.../partial-credit/statuses` | Status configuration — edit pill colour, text, sort order |
| `/admin/.../partial-credit/email-templates` | Email-template editor (Edit + Preview tabs) |

xlsx export hard-caps at 5,000 requests via `TooManyRowsException` (→ 413).
Two sheets: **Requests** (one row per request) + **Lines** (one row per
missing/wrong/encumbered line, joined to its parent request).

## Sales-rep on-behalf flow

`/api/v1/salesrep/partial-credit/...` — gated by
`hasAnyRole('SalesRep','Administrator')`.

1. Rep opens buyer landing, sees "Submit on behalf" trigger (visible only when role includes `SalesRep`).
2. Modal: pick buyer code → pick buyer user → enter order number.
3. `createDraftOnBehalf` validates the picked user belongs to the chosen
   code (via `user_buyers` → `buyer_code_buyers`), creates a draft with
   `isAdmin=true` (bypassing the buyer-code-ownership junction check),
   stamps `is_on_behalf=TRUE`, `on_behalf_of_id`, `on_behalf_buyer_code_id`.
4. Modal redirects to `/wholesale/partial-credit/new?draftId={id}` — the
   wizard reads the param and pre-fills + reuses the existing draft
   instead of calling `createDraft`.

**Phase 1 scoping is permissive** — any `SalesRep` can file for any active
buyer code. The eligibility filter in `OnBehalfSubmissionService` is
parked so Phase 2 can tighten by adding a `sales_representatives.user_id`
column and a `WHERE bsr.sales_rep_id = ...` clause.

## Status configuration

5 seeded rows in `partial_credit.credit_request_statuses`
(V89 seed): `DRAFT`, `PENDING_APPROVAL`, `UNDER_REVIEW`, `APPROVED`,
`DECLINED`. The admin status-config page edits cosmetic fields
(`colorHex`, `internalStatusText`, `externalStatusText`, `sortOrder`,
`showInUserCounters`). `system_status` is the immutable enum key.

Buyer landing reads `externalStatusText` + `colorHex` live from the API;
admin changes propagate without redeploy.

## Email templates

3 seeded rows in `partial_credit.email_templates` (V90 seed):
`ReviewCompleted_Approved`, `ReviewCompleted_Declined`,
`PhotoUploadRequested`. Bodies use `{{varName}}` substitution rendered by
`EmailTemplateService`:
- HTML body escapes substituted values by default; `{{!varName}}` opts
  out for admin-trusted raw HTML.
- Subject + plain-text body never escape (no HTML context).
- Missing variables substitute to empty + warn log.
- In-process `ConcurrentHashMap` cache evicted on every `update()` call.

`approvedTotalDisplay` (e.g. `"$25.00"`) carries the currency sign as
part of the rendered value — Flyway parses `${...}` as its own
placeholder syntax, so the V90 seed migration cannot put a literal `$`
adjacent to `{{`.

`PARTIAL_CREDIT_REVIEW_EMAIL_ENABLED` env var (default `true` from
Sprint 4 chunk 8) gates the actual SMTP send. When `false` the listener
logs the intended send but doesn't touch `EmailSender`.

## Audit trail

`partial_credit.email_audit` — one row per send attempt
(`template_key`, `recipient_email`, `credit_request_id`, `sent_at`,
`success`, `error_message`). Diagnose "did the buyer receive the email?"
without mining stdout. `credit_request_id` is `ON DELETE SET NULL` so
deleting a request doesn't lose the audit trail.

## What's NOT in Phase 1 (deferred to Phase 2)

- Automated Prolog encumbrance check (admin still enters manually)
- RMA auto-creation for accepted encumbered lines
- Oracle write-back of approved credits
- Datadog APM dashboards
- R-2 certification gating in wizard Step 1
- Reason-pivoted admin landings (Missing-only, Wrong-only)
- Per-column filter pop-overs on admin landing
- S3 object storage for photos (still `bytea` in V89's photo table)
- Promote the `PartialCredit_*` role tier (orphaned in V89, gated path
  documented in V90)
- Email template versioning + rollback UI
- WYSIWYG editor for email templates (raw HTML + Preview tab today)
- Free-form reviewer notes column (only completion date + reviewer id
  surfaces today)
- Buyer-side notification preferences (opt-out of emails)
- Tighter on-behalf scoping via `sales_representatives.user_id` mapping
