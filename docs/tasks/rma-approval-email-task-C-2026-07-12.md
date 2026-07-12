# Task C — RMA approval email (`SUB_SendEmail_RMAApproved`) + V93 template

Part of gap-analysis #3 "Make RMA Functional". On an **APPROVED** RMA
review-completion, send the buyer an approval email through the shipped
unified-email backbone, seeded by a new V93 template. Attaches as an
`AFTER_COMMIT` listener to `RmaReviewCompletedEvent` (already on `main` from
Task B0). `RmaService`/`completeReview` is **not** modified — the event is a
stable seam.

## Locked decisions
- **APPROVED-only.** `DECLINED` → do nothing.
- **No separate `enabled` config gate.** Rely on `EmailService`'s
  `LoggingEmailSender` in dev (logs, never sends) + the template's `enabled`
  flag. (Unlike `ReviewCompletedEmailListener`, which keeps its own PC flag.)
- Money vars formatted 2-decimal `$#,##0.00` (app-wide currency convention).
- **Swallow all send exceptions** — a failed email must never escape the async
  listener or affect the already-committed review.
- V93 is the next free migration number (highest on `main` is V92).

## Files
1. `backend/.../db/migration/V93__seed_rma_approved_email_template.sql` — seed
   one `email.template` row (`template_key='RMA_Approved'`, `enabled=true`),
   idempotent via `ON CONFLICT (template_key) DO NOTHING`. `from_address` /
   `reply_to` left NULL to match the PC templates' seed (V92 copied them with
   NULL — `EmailService.resolveFrom` falls back to `smtp_config` at send time).
2. `backend/.../listener/rma/RmaApprovedEmailListener.java` — mirror
   `ReviewCompletedEmailListener`'s transaction shape (`@Async(EMAIL_EXECUTOR)`
   + `@Transactional(REQUIRES_NEW)` **not** `readOnly` — `sendTemplated` writes
   `email.log` + `@TransactionalEventListener(AFTER_COMMIT)`), reusing the
   `EcoATMDirectUserRepository.findActiveEmailsByBuyerCodeId` recipient resolver
   and `BuyerCodeLookupService.findCodeById` for the `buyerCode` var.

## `vars` map (from the reloaded `Rma`)
| key | source |
|---|---|
| `rmaNumber` | `rma.getNumber()` |
| `buyerCode` | `buyerCodeLookup.findCodeById(buyerCodeId)` (fallback `""`) |
| `approvedQty` | `rma.getApprovedQty()` (fallback `0`) |
| `approvedSkus` | `rma.getApprovedSkus()` (fallback `0`) |
| `approvedTotalDisplay` | `$#,##0.00` of `rma.getApprovedSalesTotal()` (fallback ZERO) |
| `approvedItemsSummary` | `Approve`-status items joined `imei — returnReason` (legacy `SUB_SendEmail_RMAApproved` listed approved items only) |

Dispatch:
```java
emailService.sendTemplated("RMA_Approved", vars,
    new SendOverrides(recipients, null, null),
    new SourceRef("RMA", rmaId));
```
Recipients travel via `SendOverrides.to` (the seeded template has
`to_default=null`, so a null override would make `sendTemplated` throw
"no recipients").

## Guards (all → log + return, never throw)
`outcome != APPROVED` (debug) · null `rmaId` (warn) · RMA not found (warn) ·
no recipients (warn) · any `sendTemplated` exception (error, swallowed).

## Tests
- `RmaApprovedEmailListenerTest` (unit, Mockito) — mirror
  `ReviewCompletedEmailListenerTest`: APPROVED dispatch (key + overrides +
  source + vars via `ArgumentCaptor`, incl. `$#,##0.00` formatting), DECLINED
  no-send, null-id, not-found, no-recipients, `sendTemplated`-throws-swallowed.
- `V93MigrationIT` (real Postgres, mirror `V92MigrationIT`) — `RMA_Approved`
  row exists in `email.template`, enabled.
- `RmaApprovedEmailMigrationIT` (real Postgres, mirror
  `PartialCreditEmailMigrationIT`) — publish an APPROVED
  `RmaReviewCompletedEvent` in a committed `TransactionTemplate` tx → exactly
  one `email.log` row (`source_module='RMA'`, `status='SENT'` via
  `LoggingEmailSender`). `EcoATMDirectUserRepository` swapped for a
  `@Primary`/`@TestConfiguration` Mockito mock.

## Docs
`docs/app-metadata/modules.md` (RMA → approval-email listener + V93) and
`docs/testing/coverage.md` (new test files).
