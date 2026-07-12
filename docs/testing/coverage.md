# Test Coverage Report

Target coverage: 80%+ across all modules.

---

## auctions.reservebid (new 2026-04-22)
Target 85%+. Upload + sync branches are the load-bearing paths; see `ReserveBidServiceTest` + `ReserveBidRepositoryIT` + `ReserveBidControllerIT` + `reserveBid.spec.ts`.

---

## auctions.purchaseorder (new 2026-04-25)
Target 85%+. Upload + push paths are the load-bearing branches; see
`PurchaseOrderServiceTest` + `PODetailServiceTest` +
`PurchaseOrderControllerIT` + `PurchaseOrderSnowflakePushListenerTest` +
`admin-purchase-orders.spec.ts`.

---

## auctions.purchaseorder.weekrange-overlap (new 2026-07-11, gap 0.1)
Target 85%+. VAL_WeekRange_PO — GLOBAL week-range overlap guard rejecting a
create/update whose `[weekFrom, weekTo]` intersects ANY existing PO's range
(no product/grade/buyer scoping), so 4C never sees two PO floor candidates for
one (product, grade, week). Load-bearing branches: the inclusive-interval
overlap predicate, GLOBAL scope (overlap flagged across different
buyer/product/grade), non-overlap allowed, and exclude-self on update. The
guard compares the **business `weekId`** (chronological), not the surrogate
`mdm.week.id` (the V65 seed does not assign the surrogate in calendar order —
proved during development). See `PurchaseOrderValidatorTest` (+3 unit cases:
overlap-throws / non-overlap-passes / update-forwards-exclude-id) and
`PurchaseOrderOverlapRepositoryIT` (6 cases on real Postgres — weekId-is-
chronological diagnostic + same-span + global-across-buyer/product/grade +
boundary-shared-week + non-overlap + update-excludes-self).

---

## auctions.recalc (new 2026-04-30)
Target 85%+. RANKING + TARGET_PRICE are the load-bearing branches; see
`BidRankingRepositoryIT` + `TargetPriceRecalcRepositoryIT` +
`BidRankingServiceTest` + `TargetPriceRecalcServiceTest` +
`RecalcOrchestratorTest` + `RecalcRoundClosedListenerTest` +
`RecalcAdminControllerIT` + `RecalcEndToEndIT` +
`BidRankingSnowflakePushListenerTest` + `TargetPriceSnowflakePushListenerTest`.

---

## auctions.r2init (new 2026-05-06)
Target 85%+. Qualification CTE + special-treatment CTE + QBC bulk INSERT
+ special-buyer bid_data bulk INSERT are the load-bearing branches; see
`R2BuyerQualificationRepositoryIT` + `R2SpecialBuyerRepositoryIT` +
`BidDataForAllAERepositoryIT` + `QualifiedBuyerCodeRepositoryIT` +
`R2BuyerAssignmentServiceTest` + `R2BuyerAssignmentListenerTest` +
`R2BuyerAssignmentAdminControllerIT` + `R2BuyerAssignmentEndToEndIT`.

---

## auctions.r3lifecycle (new 2026-05-07)
Target 85%+. R3 qualification CTE + STB CTE + QBC three-set INSERT +
round3 reports INSERT + predecessor guard + has_round=false SKIPPED branch
are the load-bearing paths; see
`R3PreProcessSupportRepositoryIT` + `R3BuyerQualificationRepositoryIT` +
`R3SpecialBuyerRepositoryIT` + `Round3BuyerDataReportRepositoryR3IT` +
`QualifiedBuyerCodeRepositoryR2IT` (extended R3 case) +
`R3PreProcessServiceTest` + `R3InitServiceTest` +
`R3PreProcessListenerTest` + `R3InitListenerTest` +
`R3LifecycleAdminControllerIT` + `R3LifecycleEndToEndIT`.

---

## auctions.biddata.row-visibility (new 2026-05-07)
Target 85%+. 10 R2 tests (7 Only_Qualified branches + 1 DW + 1 All_Buyers + 1 noPriorBid_invisible) + 7 R3 tests + 2 STB + 1 R1 = 20 total.
See `BidDataCreationRepositoryIT` (20 new cases added by sub-project 5b) and
`BidDataScenario` builder extensions (7 new fluent primitives).

---

## partialcredit.review-completed-email (new 2026-05-11)
Target 85%+. 7 unit cases cover both the `partial-credit.review-completed-email.enabled=false`
(log-only) and `enabled=true` (real send) modes, plus the four
degrade-gracefully paths (null id, request not found, no recipients, sender
throws). See `ReviewCompletedEmailListenerTest`.

## partialcredit.e2e-admin (new 2026-05-11)
Playwright smoke for the admin review surface: `admin-partial-credit-review.spec.ts`
runs 2 cases (landing renders + status-config colour edit round-trip) and
keeps 1 happy-path test `.skip`'d until the JDBC Snowflake reader lands
in staging. Frontend webServer config auto-starts `npm run dev`; the
spec skips when `isBackendAvailable()` returns false.

---

## partialcredit.sprint4 (new 2026-05-12)
Target 85%+. Sprint 4 closes out Phase 1 with 8 additive chunks; the
test sweep covers each surface end-to-end.

| Surface | Key tests |
|---|---|
| V90 migration (email_templates + email_audit + on-behalf cols) | `V90MigrationIT` (5 cases) — table set, seed presence, audit indexes, on-behalf columns + default, PartialCredit_* roles remain orphaned |
| ~~EmailTemplateService — render / cache / update / preview~~ | `EmailTemplateServiceTest` (11) — HTML escape default, `{{!varName}}` raw opt-out, missing-variable warn-log, `$`-in-substitution regression guard for `Matcher.appendReplacement`. **Deleted by Task 11** (2026-07-11) — the service was fully orphaned once the listener + controller stopped calling it; the render rules it proved now live in the shared `TemplateRenderer`, covered by `TemplateRendererTest` |
| ~~ReviewCompletedEmailListener flipped to DB templates~~ | `ReviewCompletedEmailListenerTest` (7) — mocked `EmailTemplateService`, asserted variable map shape + audit row writes. **Rewritten by Task 11** — see "partialcredit.email-migration" below for the current shape |
| EmailAuditService | `EmailAuditServiceIT` (3) — success / failure / batch persistence on real Postgres. Still green post-Task-11 (table + service frozen, D5 — the listener stopped calling it, nothing else did) |
| ~~Admin email-templates REST~~ | `AdminPartialCreditControllerIT` extension (+6) — list / patch / preview happy paths plus 401 + 404. **Removed by Task 11** — the endpoints are gone, superseded by `AdminEmailControllerTemplatesIT` (see "email.admin-templates" below) |
| CreditRequestPhotoService — upload / list / download / delete | `CreditRequestPhotoServiceTest` (14) — oversize, unsupported MIME, empty upload, finalized-parent freeze, per-line cap, DAMAGE-bypasses-cap, buyer-vs-admin delete authorization, byte-snapshot regression for `MultipartFile.getBytes()` |
| Photo REST endpoints | `BuyerPartialCreditControllerIT` extension (+6) — 201 multipart, 413 body shape, list, inline-disposition download, 204 delete, 403 foreign-delete |
| Buyer detail page components | RTL: `BuyerLineSection.test.tsx` (5) + `PhotoUploadDropzone.test.tsx` (4) + `PhotoGallery.test.tsx` (6) |
| OnBehalfSubmissionService | `OnBehalfSubmissionServiceTest` (9) — listings pass-through, createDraftOnBehalf happy path, user-not-associated-with-code 403, validation negatives |
| OnBehalfPartialCreditController | `OnBehalfPartialCreditControllerIT` (10) — 200/403 matrix across the three endpoints |
| OnBehalfModal (frontend) | `OnBehalfModal.test.tsx` (7) — 3-step picker walk-through, Create disabled until order# non-blank, server-error inline render, Back button retains user-picker state |
| PartialCreditExcelExportService | `PartialCreditExcelExportServiceTest` (7) — two-sheet structure, empty result, Requests body, mixed-reason Lines sheet (Wrong row uses `expected_ecoatm_code`), null reviewDecision renders `"PENDING"`, over-cap throws with `matched` count, exactly-at-cap allowed |
| xlsx endpoint | `AdminPartialCreditControllerIT` extension (+3) — 200 with attachment header, 413 body shape, 401 unauth |
| CreditRequestFileDropParser | `CreditRequestFileDropParserTest` (11) — csv first-column, xlsx first-sheet, docx whitespace split, short-digit-run drop, dedupe, quoted-cells, empty file warning, unsupported MIME, keep-rule unit |
| parse-barcodes endpoint | `BuyerPartialCreditControllerIT` extension (+2) — 200 with warnings, 415 unsupported type |
| End-to-end smoke | `partial-credit-sprint4.spec.ts` (Playwright, was 6 cases covering five Sprint 4 entry points + bidder-can't-reach-admin; **Task 11 removed the email-templates-page case** — now 5 cases / four entry points + bidder-can't-reach-admin) |

Full backend partial-credit sweep: **124/124 green** (was 41 pre-Sprint-4).
Frontend RTL: 30 new component cases across the four Sprint 4 test files.

---

## partialcredit.email-migration (Task 11, 2026-07-11)
Target 85%+. Final task of the unified-email-management build — repoints
`ReviewCompletedEmailListener` from its own render/send/audit path onto
`EmailService.sendTemplated` and retires the PC-specific template
editor. Load-bearing branches: the exact `SendOverrides`/`SourceRef`
shape passed to `EmailService`, template-key selection by outcome, every
degrade-gracefully guard (disabled flag, null id, request not found, no
recipients), any `sendTemplated` exception being swallowed, and — the
one only a real-Postgres IT can prove — the listener's `@Transactional`
attribute actually allows the `email.log` INSERT (it must NOT be
`readOnly`, since `sendTemplated` joins the listener's `REQUIRES_NEW`
transaction and writes).

| Surface | Key tests |
|---|---|
| `ReviewCompletedEmailListener` → `EmailService` | `ReviewCompletedEmailListenerTest` (7, rewritten) — mocks `EmailService` only (dropped `EmailSender`/`EmailTemplateService`/`EmailAuditService`); `eq(new SendOverrides(recipients,null,null))` + `eq(new SourceRef("PARTIAL_CREDIT", requestId))` proof for both APPROVED (`ReviewCompleted_Approved`, includes `approvedTotalDisplay`) and DECLINED (`ReviewCompleted_Declined`, omits it); disabled-flag/null-id/not-found/no-recipients all assert `sendTemplated` is never called; a thrown `IllegalStateException` (simulating a disabled template) is swallowed and logged, never escapes |
| Real end-to-end wiring + the readOnly-transaction fix | `PartialCreditEmailMigrationIT` (2, new, real Postgres via `PostgresIntegrationTest`) — the 3 PC keys exist in `email.template` post-V92; publishing a `ReviewCompletedEvent` inside a `TransactionTemplate`-committed transaction (mirrors `AggInventorySyncListenerIT`'s pattern for testing a real `@TransactionalEventListener(AFTER_COMMIT)` + `@Async` listener) drives the real listener → real `EmailService` → writes one `email.log` row (`source_module='PARTIAL_CREDIT'`, `status='SENT'` via the default `LoggingEmailSender`) and **zero** new `partial_credit.email_audit` rows. `EcoATMDirectUserRepository` is swapped for a `@Primary` Mockito mock (`@TestConfiguration`, not `@MockBean`) so the test doesn't have to build the buyer/account/direct-user join chain just to resolve one recipient |
| Admin controller | `AdminPartialCreditControllerIT` — 6 email-template cases removed (endpoints deleted); remaining 20 cases green, untouched |
| E2E | `partial-credit-sprint4.spec.ts` — the email-templates-page case removed; remaining 5 cases green |

Full partial-credit backend sweep post-Task-11: **220/220 green** (20
`AdminPartialCreditControllerIT` + 18 `BuyerPartialCreditControllerIT` +
10 `OnBehalfPartialCreditControllerIT` + 2 `PartialCreditEmailMigrationIT`
+ 7 `ReviewCompletedEmailListenerTest` + 4 `PartialCreditMigrationIT` + 5
`V90MigrationIT` + 16 `ActionRecommendationServiceTest` + 16
`AdminCreditRequestServiceTest` + 15 `CreditCalculationServiceTest` + 12
`CreditRequestFileDropParserTest` + 15 `CreditRequestPhotoServiceTest` +
17 `CreditRequestServiceTest` + 21 `CreditRequestValidatorTest` + 3
`EmailAuditServiceIT` + 7 `MaxSubmittedBidLookupTest` + 9
`OnBehalfSubmissionServiceTest` + 7 `PartialCreditExcelExportServiceTest`
+ 8 `ResolveReceivedDeviceServiceTest` + 8 `StatusConfigServiceTest`).
Broader unified-email regression sweep (Tasks 1-10's suites, to confirm
no collateral damage from adding a new `EmailService` caller): **123/123
green**. `EmailTemplateService`/`EmailTemplateServiceImpl` +
`EmailTemplateServiceTest` deleted (confirmed fully unreferenced by grep
before deletion); the 3 now-orphaned PC template DTOs
(`dto.partialcredit.EmailTemplateView`/`EmailTemplateUpdate`/
`EmailTemplatePreviewRequest`) deleted alongside them.
`partial_credit.email_templates`/`email_audit` tables, the
`EmailAuditService` bean, and the `model.partialcredit.EmailTemplate`
entity are untouched (frozen, D5).

**Frontend:** the retired route's own RTL file
(`EmailTemplateEditor.test.tsx`, 8 cases) was deleted along with the
component — no orphaned test remains (confirmed: `npm test -- --run`
finds no reference to `EmailTemplateEditor`/`adminEmailTemplatesClient`).
`npm test -- --run`: **283/285 green, 31/32 test files** — the 2
failures are the same pre-existing, unrelated `apiFetch-guard.test.ts`
cases called out in the Task 10 entry above (none of the flagged files
were touched by Task 11 either). `npm run build`'s TypeScript gate is
still blocked by the same pre-existing `AdminReviewClient.tsx` /
`/wholesale/partial-credit/new` errors from the Task 10 entry — verified
unrelated to Task 11 (neither file is in this task's diff, and the
Turbopack module-graph compile step itself succeeds, proving the route
deletion left no dangling import).

---

## email.admin-smtp (new 2026-07-11, Task 7)
Target 85%+. First admin surface of the unified email module — the
security-critical assertions carry the weight.

| Surface | Key tests |
|---|---|
| `AdminEmailController` SMTP endpoints | `AdminEmailControllerSmtpIT` (9, `@WebMvcTest` + imported `SecurityConfig`) — D2: GET never returns `password`/`encryptedPassword`; PUT structurally drops a client-supplied `password`/`encryptedPassword` (ArgumentCaptor equals the 11 real fields only); `@Valid` rejects `serverPort:0` as 400 without calling the service; authz matrix — Bidder→403 on GET **and** PUT **and** `/smtp/test`, plus no-token→401; `/smtp/test` rate-limit denial→429; `/smtp/test` graceful no-`JavaMailSender`-bean branch→`{success:false,"SMTP is not configured"}` |
| `SmtpConfigService.update` | `SmtpConfigServiceTest` (+3, now 7 total) — full-patch + audit-stamp + cache-invalidation (findById called 3×: prime + update + post-update reload), all-null patch leaves columns unchanged, missing-singleton-row guard throws |

Backend email-admin sweep: **16/16 green** (`AdminEmailControllerSmtpIT`
9 + `SmtpConfigServiceTest` 7).

---

## email.admin-templates (new 2026-07-11, Task 8)
Target 85%+. Extends `AdminEmailController` (Task 7) with the
`email.template` CRUD/preview/send-test surface. Load-bearing branches:
duplicate-`templateKey` conflict, `templateKey` immutability on update,
404 on every missing-id path, preview bypassing the `enabled` check, and
the send-test rate limit being user-keyed (not IP-keyed) and checked
before the template lookup.

| Surface | Key tests |
|---|---|
| `AdminEmailController` template endpoints | `AdminEmailControllerTemplatesIT` (22, `@WebMvcTest` + imported `SecurityConfig`, mirrors `AdminEmailControllerSmtpIT`'s auth setup) — create→201+id with audit stamps; duplicate key→409; list; get by id 200/404; put updates `changed_date` + ignores a submitted `templateKey` change (ArgumentCaptor asserts the saved entity kept the original key); delete→204/404; preview renders `{{var}}` via mocked `TemplateRenderer` **on a disabled template** (proves the enabled-check bypass) + null-`contentPlain`→`text` absent; send-test→200 + `ArgumentCaptor` proof of the exact `SendOverrides`/`SourceRef` passed to `EmailService.sendTemplated`, plus an exact-match assertion that the rate-limit key is `"email-send-test:" + userId`; send-test 404 (missing template) and 429 (limiter denies, asserted to skip the template lookup entirely); non-admin→403 on list/create/send-test; validation negatives — bad `templateKey` pattern→400, blank `subject`→400, bad/blank send-test email→400 |
| `AdminEmailControllerSmtpIT` regression | Extended with 3 new `@MockBean`s (`EmailTemplateRepository`, `TemplateRenderer`, `EmailService`) required after `AdminEmailController`'s constructor grew — still 9/9 green, no behavior changes |

Backend email-admin sweep: **31/31 green** (`AdminEmailControllerTemplatesIT`
22 + `AdminEmailControllerSmtpIT` 9).

---

## email.admin-log (new 2026-07-11, Task 9)
Target 85%+. Completes `AdminEmailController` (Tasks 7-8) with the
`email.log` delivery-log list/detail/resend surface — the last backend
admin task for this module. Load-bearing branches: filter params parsed
and forwarded verbatim (incl. default page/size/sort when omitted), an
unrecognized `status` value 400s rather than 500ing, detail exposes the
rendered `content_html` snapshot, and resend resets
`retry_count`/`next_attempt_at` and saves that reset **before** calling
`EmailService.resend` (the admin count-bypass, design §5) — proven via an
`InOrder` Mockito verification, not just the end state. The
`EmailLogRepository.search` JPQL also needed a real-Postgres fix mid-task:
a bare `:from IS NULL`/`:to IS NULL` branch (no comparison operator to
give PostgreSQL a type to infer) tripped `PSQLException: could not
determine data type of parameter $N` under the extended query protocol —
caught by `EmailRepositoryIT` against real Postgres, invisible to the
mocked `@WebMvcTest` slice. Fixed by wrapping those two null-checks in
`CAST(... AS timestamp)`.

| Surface | Key tests |
|---|---|
| `AdminEmailController` log endpoints | `AdminEmailControllerLogIT` (11, `@WebMvcTest` + imported `SecurityConfig`, mirrors the Smtp/Templates IT auth setup) — list with no params defaults to page 0/size 20/sort `createdDate desc` with all-null filters forwarded to `search`; list with every filter set (`status`/`from`/`to`/`templateKey`/`page`/`size`) asserts the exact parsed values reach `search` via `ArgumentCaptor`; invalid `status` → 400, `search` never called; detail 200 with `contentHtml` snapshot / 404 when missing; resend — `InOrder` proof of `findById` → `save` (captured: `retryCount==0`, `nextAttemptAt==null`) → `EmailService.resend(id)`, and 404 skips both `save` and `resend` when the id is missing; non-admin → 403 on list/detail/resend; unauthenticated → 401 |
| `EmailLogRepository.search` | `EmailRepositoryIT` (+1 case, now 10 total) — real Postgres: filters by `templateKey` (proves newest-first ordering too), by `status`+`templateKey` together, by date range scoped to a `templateKey`, by `status` alone (loose containment — shared dev-DB fallback may hold unrelated rows), unfiltered reachability, and paging (`page size 1` of an exactly-2-row scoped set returns the right row with correct `totalElements`/`totalPages`) |
| `AdminEmailControllerSmtpIT` / `AdminEmailControllerTemplatesIT` regression | Both extended with a new `@MockBean EmailLogRepository` required after `AdminEmailController`'s constructor grew again — still 9/9 and 22/22 green, no behavior changes |

Backend email-admin sweep: **52/52 green** (`AdminEmailControllerLogIT` 11 +
`AdminEmailControllerSmtpIT` 9 + `AdminEmailControllerTemplatesIT` 22 +
`EmailRepositoryIT` 10). Regression sweep of untouched email unit tests
(`EmailServiceTest` 16, `EmailRetryWorkerTest` 5, `SmtpEmailSenderTest` 10,
`LoggingEmailSenderTest` 1, `EmailMessageTest` 8,
`ReviewCompletedEmailListenerTest` 7 — 47 total) stayed green, confirming
no collateral behavior change.

---

## emailadmin.frontend (new 2026-07-11, Task 10)
RTL coverage for the Email Admin frontend (`frontend/src/app/(dashboard)/admin/app-control-center/email-admin/`), each tab component rendered in isolation. 13 cases across 3 files:

| Surface | Key tests |
|---|---|
| `SmtpConfigTab` | `emailAdmin.smtp.test.tsx` (4) — loads `GET /smtp` and renders camelCase fields with no Username/Password inputs (D2); `PUT /smtp` body is asserted `.toEqual(...)` against the exact `SmtpConfigUpdate` shape plus explicit `not.toHaveProperty` guards against snake_case/password leakage; `Enabled` toggle flips the saved payload; `Test Connection` posts to `/smtp/test` and surfaces `{success, message}` via the `onBanner` callback |
| `TemplatesTab` + `TemplateDetailEditor` | `emailAdmin.templates.test.tsx` (5) — lists via `GET /templates`; edit sends a camelCase `PUT .../templates/{id}` body (`EmailTemplateUpsert` shape, guarded against `template_name`/`content_html`/`has_attachment`); create sends `POST` with `templateKey` + `contentHtml` populated (the stub's create flow had neither field wired and would have 400'd on every attempt); delete confirms then `DELETE`s; Preview renders `{subject, html, text}` and Send Test posts `{toAddress, vars}` to `/templates/{id}/send-test` — both previously entirely unwired |
| `EmailLogTab` | `emailAdmin.log.test.tsx` (4) — lists from the `Page<EmailLogView>` envelope (`.content`/`.totalElements`); status filter re-fetches with the real `PENDING`/`SENT`/`FAILED` enum (the stub used the non-existent `SENT/QUEUED/FAILED/ERROR`); **M-3** — opening a detail row sanitizes `contentHtml` via `DOMPurify.sanitize`, asserting a `<script>` tag and a dangling `onerror` attribute are both absent from the rendered DOM (`container.querySelector('script')` / `'[onerror]'` are `null`); `Resend` posts to `POST /log/{id}/resend` and reloads the list |

`npm test -- emailAdmin`: **13/13 green**. Full frontend suite: 291/293 (2
pre-existing `apiFetch-guard.test.ts` failures predate this task and are
unrelated — verified none of the violation files were touched here).
`npm run build`'s TypeScript gate is currently blocked by pre-existing,
unrelated errors in `partial-credit/[id]/AdminReviewClient.tsx` and
`/wholesale/partial-credit/new` (confirmed via `git stash` to reproduce
on `HEAD` with this task's changes removed); this task's own files carry
**zero** errors under a full `tsc --noEmit` sweep and were confirmed to
render correctly end-to-end in a real authenticated browser session
(Playwright) — see `docs/tasks/email-management-implementation-plan-2026-07-10.md`
Task 10 for detail.

---

## rma.submit-offeritem-match (new 2026-07-11, gap 0.2 · VAL_RMARequestFile)
Target 85%+. RMA submit now matches each uploaded IMEI/serial against a shipped
`pws.offer_item` owned by the buyer code (`imei_detail → offer_item →
offer.buyer_code_id`, the modern port of legacy `IMEIDetail → offeritem_buyercode
→ OfferItem`), populating `device_id` / SKU (via device) / `sale_price` +
best-effort order number + ship date on the line, and rolling up
`request_qty` / `request_skus` (distinct devices) / `request_sales_total`.
Unmatched IMEIs reject the whole submission (legacy all-or-nothing
`IsValidRMA=false`).

| Surface | Key tests |
|---|---|
| `RmaService.submitRmaRequest` matching (Mockito) | `RmaServiceTest$SubmitRmaRequest` (3) — matched lines carry `deviceId`/`salePrice` + non-zero roll-ups (qty/skus/sales-total via `ArgumentCaptor`); match-on-`serial_number` branch; unmatched line → `success=false`, error names the IMEI, and **neither** `rmaRepository.save` nor `rmaItemRepository.save` is called |
| End-to-end match (real Postgres) | `RmaSubmitOfferItemMatchIT` (2, extends `PostgresIntegrationTest`) — seeds buyer_code + offer + offer_item + imei_detail + device + order; matched IMEI → accepted, persisted line carries `device_id`/sale price/order number/ship date, roll-ups non-zero, SKU resolves on the detail projection; unknown IMEI → rejected, RMA count unchanged |

Match source decision: **local `pws.imei_detail` + `pws.offer_item`**, not the
Snowflake `VW_SALE_ORDER_SHIPMENT` reader — the local chain carries the
IMEI→device/price data, is the faithful legacy port, and is seedable in a
Postgres IT; the Snowflake reader is order-number-scoped, returns empty in dev,
and cannot be seeded in a Postgres IT.

---

## rma.oracle-create (new 2026-07-12 · RMA #3 Task B0)
Target 85%+. Makes an Approved RMA review create the RMA order in Oracle,
event-driven so the follow-on approval-email + Snowflake tasks attach to the
same `RmaReviewCompletedEvent`. Load-bearing branches: the event publish +
outcome mapping in `completeReview`; `submitRma` reusing the hardened
fail-closed/SIM gate; the `oracle_*` column write on success (`returnCode "00"`
→ number/id/http/json/status + `is_successful=true`) vs failure (blank code →
status captured, `is_successful=false`, no number, review NOT rolled back); the
listener's outcome/enabled/null-id guards + exception swallow; and the secured
resubmit endpoint (wrong-role → 403).

| Surface | Key tests |
|---|---|
| `RmaService.completeReview` event publish | `RmaServiceTest$CompleteReview` — APPROVED and DECLINED both publish an `RmaReviewCompletedEvent` (ArgumentCaptor asserts rmaId/outcome/reviewerUserId/occurredAt) |
| `OracleOrderClient.submitRma` | `OracleOrderClientTest` (+4, now 12) — qa/production fail-closed + no-profile/dev SIM success, reusing the same `offlineOrErrorResponse()` gate as `submitOrder` |
| `RmaOraclePayloadBuilder` | `RmaOraclePayloadBuilderTest` (3) — header + approved-line JSON shape, empty-line array, null price/SKU → empty strings |
| `RmaOracleService.createRmaInOracle` | `RmaOracleServiceTest` (4) — success writes SIM columns; failure captures status w/o number (no throw); only-approved lines feed the payload; RMA-not-found throws before `submitRma` |
| `RmaOracleCreateListener` | `RmaOracleCreateListenerTest` (5) — APPROVED triggers create; DECLINED/disabled/null-id skip; a thrown create is swallowed (never rethrows) |
| Resubmit endpoint | `RmaControllerTest` (+6, now 25) — 200 rewrites oracle_* (success + oracle-failure body), Bidder → 403 (matcher + `@PreAuthorize`), no-token → 401, not-found → 404 |
| Real-Postgres column write | `RmaOracleCreateIT` (1, `dev` profile → SIM) — seeds an approved RMA, calls `createRmaInOracle`, asserts `oracle_*` + `json_content` persist on the real schema |

Full RMA + Oracle backend sweep: **87/87 green** (`RmaControllerTest` 25 +
`RmaServiceTest` 25 + `OracleOrderClientTest` 12 + `RmaDeposcoSyncServiceTest`
10 + `RmaOracleCreateListenerTest` 5 + `RmaOracleServiceTest` 4 +
`RmaOraclePayloadBuilderTest` 3 + `RmaSubmitOfferItemMatchIT` 2 +
`RmaOracleCreateIT` 1). `OfferServiceTest` 36 green (order path unaffected by
the shared `postToOracle` refactor).

> **Environment note:** running any `PostgresIntegrationTest` required fixing a
> pre-existing repeatable-migration bug — `R__apply_triggers.sql` referenced
> `email.email_template` (dropped by V92, which created `email.template`),
> failing every fresh-DB Flyway run. One-line fix shipped in this task's branch
> (`fix(email):`); it also unblocks the previously-green `RmaSubmitOfferItemMatchIT`.

---

## rma.snowflake-sync (new 2026-07-12 · RMA #3 Task D · SUB_SendRMADetailsToSnowflake)
Target 85%+. On a review completion, the AFTER_COMMIT
`RmaSnowflakePushListener` snapshots the RMA (header + items) and pushes it to
the `AUCTIONS.UPSERT_RMA_DATA(?)` stored proc — logging no-op in dev (default),
real JDBC in prod. Attaches to Task B0's `RmaReviewCompletedEvent`; no change to
`RmaService`/`completeReview`. Load-bearing branches: the payload snapshot
carries the full RMA field set + items; **pushes on any completion** (legacy
calls the sub-microflow on both approved AND declined branches — the DECLINED
case asserts the push still happens); the `rma.sync.enabled=false`
short-circuit; null-id + missing-RMA guards; a writer exception swallowed (never
rethrown); and the prod JDBC writer calling the exact legacy proc with the JSON
snapshot as the single `JSON_CONTENT` arg.

| Surface | Key tests |
|---|---|
| `RmaSnowflakePushListener` | `RmaSnowflakePushListenerTest` (6) — APPROVED → `writer.push` once with a full-snapshot `ArgumentCaptor` assertion (rmaId/number/buyerCode/status/salesTotal/items/imei/statusDisplay); DECLINED → still pushes (legacy pushes on both branches); `enabled=false` → no push + `verifyNoInteractions` on the repos/lookup; null-id → skip; missing RMA (`findById` empty) → skip; writer throws → swallowed (no rethrow) |
| `LoggingRmaSnowflakeWriter` (default) | `LoggingRmaSnowflakeWriterTest` (3) — snapshot serialises to a shape carrying the RMA header + items (rmaId/rmaNumber/buyerCode/systemStatus/items/imei); `push` logs + never throws; empty-item-list tolerated |
| `JdbcRmaSnowflakeWriter` (prod) | `JdbcRmaSnowflakeWriterTest` (2) — `push` calls `CALL AUCTIONS.UPSERT_RMA_DATA` with the JSON snapshot arg; a JDBC failure is wrapped with the proc name and rethrown for the listener to swallow |

RMA Snowflake sweep: **11/11 green** (`RmaSnowflakePushListenerTest` 6 +
`LoggingRmaSnowflakeWriterTest` 3 + `JdbcRmaSnowflakeWriterTest` 2). Snowflake
target `AUCTIONS.UPSERT_RMA_DATA(?)` is **confirmed** from the legacy
`PWS_UpsertRMAStoredProc` constant (`migration_context`), not best-effort.

---

## rma.approval-email (new 2026-07-12 · RMA #3 Task C)
Target 85%+. On an APPROVED `RmaReviewCompletedEvent`, sends the buyer the RMA
approval email through the unified email backbone (`EmailService.sendTemplated`),
seeded by V93 `RMA_Approved`. Attaches as a second AFTER_COMMIT listener to the
Task-B0 event — `RmaService`/`completeReview` untouched. Load-bearing branches:
the exact `SendOverrides(recipients,null,null)` / `SourceRef("RMA", rmaId)` shape
and the `vars` map (incl. `$#,##0.00` money formatting + approved-only item
summary); the APPROVED-only gate (`DECLINED` → no-op); the null-id / RMA-not-found
/ no-recipients guards; any `sendTemplated` exception swallowed; and — only a
real-Postgres IT can prove — the listener's `@Transactional(REQUIRES_NEW)` is
**not** `readOnly`, so the `email.log` INSERT commits.

| Surface | Key tests |
|---|---|
| `RmaApprovedEmailListener` → `EmailService` | `RmaApprovedEmailListenerTest` (6, Mockito) — APPROVED: `sendTemplated` called once with `eq("RMA_Approved")`, `eq(SendOverrides(recipients,null,null))`, `eq(SourceRef("RMA", rmaId))`, vars via `ArgumentCaptor` (`rmaNumber`/`buyerCode`/`approvedQty`/`approvedSkus`/`approvedTotalDisplay=="$1,234.50"`/`approvedItemsSummary` lists approved lines only); DECLINED → `verifyNoInteractions`; null-id / RMA-not-found / no-recipients → never called (+ warn logged); `sendTemplated` throwing → swallowed, never escapes |
| V93 seed | `V93MigrationIT` (1, real Postgres) — one enabled `RMA_Approved` `email.template` row whose `subject`/`content_html` carry every `{{var}}` the listener supplies |
| Real end-to-end wiring + non-readOnly tx | `RmaApprovedEmailMigrationIT` (2, real Postgres, mirrors `PartialCreditEmailMigrationIT`) — publishing an APPROVED event inside a committed `TransactionTemplate` tx drives the real listener → real `EmailService` → one `email.log` row (`source_module='RMA'`, `status='SENT'` via `LoggingEmailSender`); a DECLINED event writes none. `EcoATMDirectUserRepository` swapped for a `@Primary`/`@TestConfiguration` Mockito mock so no buyer/account join chain is needed |

RMA approval-email sweep: **9/9 green** (`RmaApprovedEmailListenerTest` 6 +
`RmaApprovedEmailMigrationIT` 2 + `V93MigrationIT` 1).

---

## buyersusers.salesrep-crud (new 2026-07-12 · gap 2.4 sub-feature 1)
Target 85%+. Write CRUD for internal sales reps porting the legacy
`Act_SaveSaleRep` (trim + case-insensitive dup guard, JWT-stamped owner/changer)
and `ACT_DeleteSalesRep` (offer-reference delete guard) microflows. Load-bearing
branches: name-trim, the case-insensitive duplicate guard (all-reps on create,
exclude-self on update), the `pws.offer` delete guard, and the dedicated
`/api/v1/admin/sales-representatives/**` authz namespace (Administrator +
SalesOps only — NOT under the Compliance-admitting `/admin/buyers/**`).

| Surface | Key tests |
|---|---|
| `SalesRepService` (Mockito) | `SalesRepServiceTest` (9) — create trims + stamps owner/changer/dates + honours `active` default/explicit; create rejects a case-insensitive dup (never saves); update excludes self in the dup check + stamps changer; update rejects a cross-rep collision; update/delete missing-id → `EntityNotFoundException`; delete throws `SalesRepHasOffersException` when an offer references the rep (never deletes) + short-circuits before the offer count on missing id; delete succeeds when none |
| `SalesRepController` (real Postgres) | `SalesRepControllerIT` (8, extends `PostgresIntegrationTest`, `@AutoConfigureMockMvc` + `@Transactional`; Long-principal auth via the `authentication(...)` post-processor mirroring `AdminEmailControllerSmokeIT`) — 403 matrix (Bidder → 403 on create/update/delete); unauth → 401; Administrator **and** SalesOps create → 201; create trims + stamps; case-insensitive duplicate → 409; blank name → 400; delete-with-offers → 409; delete-clean → 204. Owner FK satisfied via seeded dev user ids (9001 admin / 9003 salesops) |

Sales-rep CRUD sweep: **17/17 green** (`SalesRepServiceTest` 9 +
`SalesRepControllerIT` 8). Run:
`./mvnw test -Dtest=SalesRepServiceTest,SalesRepControllerIT
-Dspring.flyway.validate-on-migrate=false`. The IT boots the full Spring
context against the shared dev Postgres (pg-test profile, which already sets
`validate-on-migrate=false` + `baseline-on-migrate=true` for the checksum
drift). No new migration (table exists since V8/V18); no Snowflake push.
