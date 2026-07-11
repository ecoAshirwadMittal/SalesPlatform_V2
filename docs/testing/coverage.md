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
| EmailTemplateService — render / cache / update / preview | `EmailTemplateServiceTest` (11) — HTML escape default, `{{!varName}}` raw opt-out, missing-variable warn-log, `$`-in-substitution regression guard for `Matcher.appendReplacement` |
| ReviewCompletedEmailListener flipped to DB templates | `ReviewCompletedEmailListenerTest` (7) — mocks `EmailTemplateService`, asserts variable map shape + audit row writes on both success + sender-throws paths |
| EmailAuditService | `EmailAuditServiceIT` (3) — success / failure / batch persistence on real Postgres |
| Admin email-templates REST | `AdminPartialCreditControllerIT` extension (+6) — list / patch / preview happy paths plus 401 + 404 |
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
| End-to-end smoke | `partial-credit-sprint4.spec.ts` (Playwright, 6 cases) gated on `isBackendAvailable()` — covers the five Sprint 4 entry points + bidder-can't-reach-admin |

Full backend partial-credit sweep: **124/124 green** (was 41 pre-Sprint-4).
Frontend RTL: 30 new component cases across the four Sprint 4 test files.

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
