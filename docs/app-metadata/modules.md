# Application Modules

Inventory of major modules and their primary entities.

---

## Exchange Bid (EB)
- Source module: `ecoatm_eb`
- Primary tables: `auctions.reserve_bid`, `auctions.reserve_bid_audit`, `auctions.reserve_bid_sync`
- Purpose: per-(product_id, grade) reserve floor prices consumed by sub-project 4C target-price recalc
- Admin surface: `/admin/auctions-data-center/reserve-bids/**`

## Purchase Order (PO)
- Source module: `ecoatm_po`
- Primary tables: `auctions.purchase_order`, `auctions.po_detail`
- Purpose: weekly PO commitments authored via Excel upload, consumed by
  sub-project 4C target-price recalc as `GREATEST(...)` floor input
- Admin surface: `/admin/auctions-data-center/purchase-orders/**`
- Snowflake sync: push-only via `AUCTIONS.UPSERT_PURCHASE_ORDER`

## Bid Ranking + Target-Price Recalc (4C)
- Source modules: AuctionUI (`ACT_TriggerBidRankingCalculation`, `ACT_CalculateTargetPrice`)
- Primary tables: `auctions.scheduling_auctions` (status flags), `auctions.bid_ranking_config` (`include_reserve_floor`), `auctions.bid_data` (rank columns), `auctions.aggregated_inventory` (target-price columns)
- Trigger: `RoundClosedEvent` for round ∈ {1, 2}
- Admin recovery: `/admin/auctions/scheduling-auctions/{id}/re-rank` and `.../recalculate-target-price`
- Snowflake sync: per-process push of full `(week, R+1)` slice to `AUCTIONS.BUYER_BID` and `AUCTIONS.TARGET_PRICE_AUDIT`

## R2 Buyer Assignment (Sub-project 5)
- Source modules: AuctionUI (`SUB_AssignRoundTwoBuyers`, `SUB_GenerateRound2QualifiedBuyerCodes`, `Sub_ProcessSpecialBuyers`, `SUB_CreateBidDataForAllAE`, `SUB_IsSpecialTreatmentBuyer`)
- Primary tables: `auctions.scheduling_auctions` (R2-init status flags from V83), `buyer_mgmt.qualified_buyer_codes` (V72-flattened — three-set write per SA), `auctions.bid_data` (special-buyer rows seeded across every AE), `auctions_feature_config.calculate_round2_buyer_participation` (config gate)
- Trigger: `RoundStartedEvent` for round = 2
- Admin recovery: `POST /admin/auctions/scheduling-auctions/{id}/reassign-r2-buyers`
- Snowflake sync: none — legacy never synced QBC rows to Snowflake

## R3 Init + Pre-process (Sub-project 6)
- Source modules: AuctionUI (`ACT_Round3_SetStarted`, `SUB_InitializeRound3`, `SUB_Round3_PreProcessRoundData`, `SUB_GenerateRound3QualifiedBuyerCodes`, `SUB_ListRoundThreeBuyersDataForQualifiedBuyers`, `SUB_Round2_DeleteUnsubmittedBids`)
- Primary tables: `auctions.scheduling_auctions` (R3-lifecycle status flags from V84), `buyer_mgmt.qualified_buyer_codes` (R3 three-set write per SA), `auctions.round3_buyer_data_reports` (V85 adds `scheduling_auction_id` + `buyer_codes` columns), `auctions.bid_round_selection_filters` (V84 adds three R3-qualification knobs)
- Trigger: `R3PreProcessService` on `RoundClosedEvent(round=2)`; `R3InitService` on `RoundStartedEvent(round=3)`
- Admin recovery: `POST /admin/auctions/scheduling-auctions/{id}/preprocess-r3` and `.../reinit-r3`
- Snowflake sync: none — R3 QBC/report rows are not pushed to Snowflake (same policy as R2)

## Partial Credit Requests (Sprints 1-4 — Phase 1 complete 2026-05-12; email migrated onto the unified module by Task 11, 2026-07-11)
- Source module: `ecoatm_partialcredit` (Mendix)
- Schema: `partial_credit` (V89 + V90)
- Primary tables: `credit_requests` (header), `missing_device_lines` /
  `wrong_device_lines` / `encumbered_device_lines` (3 line kinds with
  reason-specific columns), `credit_request_photos` (bytea + kind),
  `credit_request_uploads` (xlsx/csv/docx evidence files),
  `credit_request_statuses` (5 seeded rows — DB-driven pill colour +
  external label). `email_templates` (3 seeded rows) and `email_audit`
  (one row per send attempt) are **frozen as of Task 11** (design
  decision D5) — V92 copied the 3 template rows into the unified
  `email.template` store and all new sends write `email.log` instead;
  these two tables + their historical rows stay in place, read-only,
  for audit history
- Purpose: lets a buyer file a partial credit claim against a recently
  shipped order with reasons of MISSING / WRONG / ENCUMBERED; sales
  ops reviews per-line, sets a decision, and the system fires a
  buyer-facing email
- Buyer surface: `/wholesale/partial-credit/**` — landing, 5-step
  wizard, read-only detail with post-submit photo upload + gallery
- Admin surface: `/admin/auctions-data-center/partial-credit/**` —
  landing with filters + status counters + xlsx export, review
  detail with per-line / per-section / global decisions, Complete
  Review modal, status configuration page. The PC-specific
  email-template editor (`.../partial-credit/email-templates`) was
  **retired by Task 11** — template editing now happens on the unified
  Email Admin screen (`/admin/app-control-center/email-admin`)
- Sales-rep surface: `/api/v1/salesrep/partial-credit/**` — Submit
  on behalf modal + endpoints (`SalesRep` role; permissive scoping
  in Phase 1)
- Status lifecycle: `DRAFT → PENDING_APPROVAL → UNDER_REVIEW →
  APPROVED | DECLINED`; photos + edits freeze at the terminal state
- Event: `AdminCreditRequestService.completeReview` publishes
  `ReviewCompletedEvent(requestId, outcome, reviewerUserId,
  occurredAt)`
- Listener: `listener/partialcredit/ReviewCompletedEmailListener` —
  `@TransactionalEventListener(AFTER_COMMIT)` + `@Async(EMAIL_EXECUTOR)`.
  **As of Task 11**, it dispatches through the unified
  `EmailService.sendTemplated(templateKey, vars, SendOverrides(recipients,
  null, null), SourceRef("PARTIAL_CREDIT", requestId))` — rendering,
  recipient-override plumbing, the `email.log` write, and delivery all
  live in `EmailService`; the listener only reloads the `CreditRequest`,
  resolves recipients, and picks `ReviewCompleted_Approved` /
  `_Declined` by outcome. Its `@Transactional` attribute is
  `REQUIRES_NEW` **without** `readOnly` (T11 dropped `readOnly=true` —
  `sendTemplated` writes `email.log` and would fail a readOnly tx)
- Gated by `partial-credit.review-completed-email.enabled` (default
  `true` from Sprint 4 chunk 8; env override
  `PARTIAL_CREDIT_REVIEW_EMAIL_ENABLED` remains for dev/staging)
- Snowflake sync: read-only — `VW_SALE_ORDER_SHIPMENT` denormalises the
  order manifest into `credit_requests.party_name` /
  `order_created_date` / `order_shipped_date` at draft creation
- Business-logic guide: `docs/business-logic/partial-credit.md`
- Phase 2 deferred items: automated Prolog encumbrance check, RMA
  auto-creation for accepted encumbered lines, Oracle write-back,
  S3-backed photos, `PartialCredit_*` role-tier promotion

## Unified Email Management (Tasks 1-11 complete — Partial Credit migrated onto this module 2026-07-11)
- Schema: `email` (V92)
- Primary tables: `smtp_config` (singleton id=1 row — server host/port/
  protocol, from/reply-to address, ssl/tls, enabled, retry + timeout;
  **no password column** — the SMTP password is env-only,
  `spring.mail.password`), `template` (keyed by `template_key`, HTML +
  plain body, from/reply-to/cc/bcc defaults; seeded from the live
  Partial Credit templates), `log` (one row per send attempt — status
  `PENDING`/`SENT`/`FAILED`, `retry_count`, `next_attempt_at`, source
  module/id)
- Admin surface:
  - Task 7 (SMTP config): `GET`/`PUT /api/v1/admin/email/smtp` +
    `POST /api/v1/admin/email/smtp/test` (rate-limited, IP-keyed)
  - Task 8 (template CRUD): `GET`/`POST /api/v1/admin/email/templates`,
    `GET`/`PUT`/`DELETE /api/v1/admin/email/templates/{id}`,
    `POST /api/v1/admin/email/templates/{id}/preview` (bypasses the
    `enabled` check), `POST /api/v1/admin/email/templates/{id}/send-test`
    (real send via `EmailService.sendTemplated`, rate-limited —
    user-keyed, not IP-keyed)
  - Task 9 (delivery log): `GET /api/v1/admin/email/log` (filtered +
    paged — `status`/`from`/`to`/`templateKey`), `GET
    /api/v1/admin/email/log/{id}` (detail incl. rendered `content_html`),
    `POST /api/v1/admin/email/log/{id}/resend` (admin-forced — bypasses
    the normal `retry_count` cap by resetting it to 0 before calling
    `EmailService.resend`)

  All `Administrator`-only. See `docs/api/rest-endpoints.md`
  § "Unified Email Management — Admin SMTP Config", § "Unified Email
  Management — Admin Template CRUD", and § "Unified Email Management —
  Admin Log"
- Security (design decision D2): the SMTP password is env-only and MUST
  NEVER appear in a request body, response, or DB column. Enforced
  structurally — `SmtpConfigView`/`SmtpConfigUpdate` have no password
  component for Jackson to populate or bind into
- `JavaMailSender` is injected as `ObjectProvider<JavaMailSender>` (not
  a hard dependency) because `spring.mail.host` is unset in this app
  today, so no bean exists yet; `/smtp/test` degrades to
  `{success:false, message:"SMTP is not configured"}` instead of
  failing the app to boot
- Task 8 `templateKey` is immutable post-create (create/update share the
  `EmailTemplateUpsert` DTO shape, but the controller only ever writes
  `templateKey` on `POST /templates`) — protects senders that resolve a
  template by key (e.g. the Partial-Credit `ReviewCompletedEmailListener`)
  from a silent break via the admin editor
- Task 9's `EmailLogRepository.search` JPQL casts the `from`/`to`
  `IS NULL` checks to `timestamp` — a bare `:from IS NULL` with no
  comparison operator in that branch leaves PostgreSQL's extended query
  protocol unable to infer the bind parameter's type
  (`PSQLException: could not determine data type of parameter $N`),
  caught by the real-Postgres `EmailRepositoryIT` rather than the mocked
  controller slice
- **Task 10 (frontend):** `frontend/src/app/(dashboard)/admin/app-control-center/email-admin/`
  — `page.tsx` is a thin shell; `SmtpConfigTab.tsx` / `TemplatesTab.tsx` /
  `TemplateDetailEditor.tsx` / `EmailLogTab.tsx` own each tab's fetching +
  UI, backed by the typed client `frontend/src/lib/adminEmailClient.ts`.
  M-3 (unsanitized `dangerouslySetInnerHTML` on the log-detail HTML
  preview) is closed with `DOMPurify.sanitize(...)` (real `dompurify`,
  not `isomorphic-dompurify` — the SSR path never calls `.sanitize()`
  since it's gated behind `useEffect`-populated state).
- **Task 11 (final migration):** `ReviewCompletedEmailListener` (Partial
  Credit) repointed onto `EmailService.sendTemplated` — see the "Partial
  Credit Requests" entry above for the wiring detail. The PC-specific
  `AdminPartialCreditController` email-template endpoints
  (`/api/v1/admin/partial-credit/email-templates/**`) and the frontend
  `.../partial-credit/email-templates` editor route (incl. an
  unsanitized `dangerouslySetInnerHTML` on its Preview tab — closed by
  deleting the route) are removed; `AdminEmailController`'s
  `/api/v1/admin/email/templates/**` is now the only template editor.
  `EmailTemplateService`/`EmailTemplateServiceImpl` (the PC-local
  render/CRUD service) were deleted as fully orphaned once nothing
  called them; the shared `TemplateRenderer` (Task 4) remains the render
  engine for both the unified and (historical) PC paths.
- **Known issue (observed 2026-07-11, not yet root-caused):** all three
  `GET /api/v1/admin/email/{smtp,templates,log}` endpoints 500
  (`"Internal server error"`, no further detail in the response) against
  the local dev DB, reproduced directly against port 8080 (not a
  frontend/proxy issue) with a valid seeded `smtp_config` row present.
  Not the JPQL casting issue above (that's `/log`-specific; this hits
  `/smtp` and `/templates` too, suggesting something controller- or
  security-config-wide). The Task 10 frontend catches and displays the
  error correctly either way (verified in a real logged-in browser
  session) — this is a backend follow-up, not a frontend defect.

## RMA — Oracle Create + Resubmit (RMA #3 Task B0, event-driven core)
- Source modules: `ecoatm_rma` (`ACT_RMADetails_CompleteReview`,
  `SUB_RMA_PrepareContentAndSendToOracle`, `SUB_RMA_PrepareOraclePayload`,
  `SUB_RMA_SendRMAToOracle`, `ACT_RMA_ReSubmitToOracle`)
- Primary tables: `pws.rma` (`oracle_number` / `oracle_id` /
  `oracle_http_code` / `oracle_json_response` / `oracle_rma_status` /
  `is_successful` / `json_content` — all scaffolded by V33/V34, no new
  migration), `pws.rma_item`
- Purpose: an Approved RMA review creates the RMA order in Oracle and records
  the response; a failed create leaves the RMA Approved with a failed Oracle
  status recoverable via the admin resubmit endpoint
- Event: `RmaService.completeReview` publishes
  `event.rma.RmaReviewCompletedEvent(rmaId, outcome, reviewedByUserId,
  occurredAt)` — `outcome` is `event.rma.RmaReviewOutcome` (`APPROVED` /
  `DECLINED`). Published inside the completing transaction so AFTER_COMMIT
  listeners fire only on commit. **Deliberately the shared seam for the two
  follow-on tasks** (approval email, Snowflake sync) — they attach as
  additional `@TransactionalEventListener(AFTER_COMMIT)` subscribers, no change
  to this module
- Listener: `listener/rma/RmaOracleCreateListener` —
  `@TransactionalEventListener(AFTER_COMMIT)` + `@Async(ORACLE_EXECUTOR)`. Acts
  only on `outcome == APPROVED`; delegates to
  `service/rma/RmaOracleService.createRmaInOracle` (the shared build → submit →
  write-`oracle_*` core, `@Transactional(REQUIRES_NEW)` and **not** `readOnly`).
  Swallows all exceptions — a failed Oracle create never rolls back the review
- Oracle client: `OracleOrderClient.submitRma(jsonPayload)` — mirrors
  `submitOrder`, POSTs to `OracleConfig.getCreateRmaPath()`, reuses the shared
  `offlineOrErrorResponse()` (SIM in local dev, fail-closed in qa/staging/prod)
  + `errorResponse()` helpers
- Payload: `service/rma/RmaOraclePayloadBuilder` mirrors
  `SUB_RMA_PrepareOraclePayload` — header (`originSystemOrderId` / `orderType`
  = `PWS-RMA` / `orderDate` / `buyerCode` / `originSystemUser`) + one
  `rmaLineItem` per APPROVED item. Exact JSON key casing is a documented
  best-effort (not recoverable from `migration_context/`; dev runs SIM)
- Admin recovery: `POST /api/v1/pws/rma/{rmaId}/resubmit-oracle` — internal
  roles only (`Administrator` / `SalesOps` / `SalesRep`; explicit SecurityConfig
  matcher precedes the broad `/api/v1/pws/rma/**` rule so Bidder is excluded,
  plus method `@PreAuthorize`); rebuilds the payload, re-runs `submitRma`,
  rewrites the `oracle_*` columns
- Config: `rma.oracle-create.enabled` (default `true`; env
  `RMA_ORACLE_CREATE_ENABLED`) — disables auto-create while leaving resubmit working
- Snowflake sync: RMA → Snowflake push, see the "RMA — Snowflake Sync" module below
- Not built here (later tasks): approval email (Task C, owns the V93 template
  migration)

## RMA — Snowflake Sync (RMA #3 Task D, event-driven)
- Source modules: `ecoatm_rma` (`SUB_SendRMADetailsToSnowflake`,
  `SUB_SendOnlyRMADetailsToSnowflake`) called from
  `ACT_RMADetails_CompleteReview`
- Primary tables: `pws.rma` + `pws.rma_item` (read-only — this module only
  snapshots and pushes; no schema change, no new migration)
- Purpose: on a review completion, push the RMA (header + line items) to
  Snowflake so the auction/reporting warehouse mirrors the RMA outcome —
  the modern port of the legacy `ExportXml` → stored-proc call
- Trigger: the shared `event.rma.RmaReviewCompletedEvent` (Task B0's seam) —
  **not** outcome-gated. Legacy `ACT_RMADetails_CompleteReview` calls
  `SUB_SendRMADetailsToSnowflake` on **both** the approved (post-Oracle-create)
  and declined branches, so this listener pushes on any completion (unlike the
  Oracle-create listener, which is APPROVED-only)
- Listener: `listener/rma/RmaSnowflakePushListener` —
  `@TransactionalEventListener(AFTER_COMMIT)` + `@Async(SNOWFLAKE_EXECUTOR)`.
  Reloads the RMA + items (via `RmaItemRepository`, not the lazy association —
  open-in-view is off), resolves the buyer-code string
  (`BuyerCodeLookupService`), builds an immutable `RmaSnowflakePayload`
  snapshot, and calls `writer.push(...)`. Swallows/logs all exceptions — a
  failed push never affects the already-committed review (no retry queue; the
  next completion re-pushes, matching the PO listener)
- Writer trio (mirrors the PO / recalc pattern):
  `service/rma/RmaSnowflakeWriter` (interface, `void push(RmaSnowflakePayload)`),
  `LoggingRmaSnowflakeWriter` (default — `@ConditionalOnProperty(rma.sync.writer,
  havingValue=logging, matchIfMissing=true)`; a no-op that logs the would-be
  row), `JdbcRmaSnowflakeWriter` (prod — `havingValue=jdbc`; calls the stored
  proc via `snowflakeJdbcTemplate`)
- Snowflake target: **`AUCTIONS.UPSERT_RMA_DATA(?)`** — a single `JSON_CONTENT`
  argument, exactly the legacy `PWS_UpsertRMAStoredProc` constant value
  (**confirmed** from `migration_context`, not best-effort). The Snowflake env
  database prefix (legacy `SnowflakeEnvironmentDB`, e.g. `ECO_QA`) is supplied
  by the connection's default DB, so it is not concatenated into the call — the
  same convention the PO / recalc JDBC writers follow
- Config: `rma.sync.enabled` (default `true`; env `RMA_SYNC_ENABLED` — `false`
  short-circuits the listener) and `rma.sync.writer` (`logging` default /
  `jdbc`). Independent of `rma.oracle-create.*`
- Business identifiers only in logs (RMA number, item count, serialised business
  snapshot) — no secrets, tokens, or the Snowflake connection string
