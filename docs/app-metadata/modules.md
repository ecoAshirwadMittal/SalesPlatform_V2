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

## Partial Credit Requests (Sprints 1-4 — Phase 1 complete 2026-05-12)
- Source module: `ecoatm_partialcredit` (Mendix)
- Schema: `partial_credit` (V89 + V90)
- Primary tables: `credit_requests` (header), `missing_device_lines` /
  `wrong_device_lines` / `encumbered_device_lines` (3 line kinds with
  reason-specific columns), `credit_request_photos` (bytea + kind),
  `credit_request_uploads` (xlsx/csv/docx evidence files),
  `credit_request_statuses` (5 seeded rows — DB-driven pill colour +
  external label), `email_templates` (3 seeded rows — DB-backed
  subject + body), `email_audit` (one row per send attempt)
- Purpose: lets a buyer file a partial credit claim against a recently
  shipped order with reasons of MISSING / WRONG / ENCUMBERED; sales
  ops reviews per-line, sets a decision, and the system fires a
  buyer-facing email
- Buyer surface: `/wholesale/partial-credit/**` — landing, 5-step
  wizard, read-only detail with post-submit photo upload + gallery
- Admin surface: `/admin/auctions-data-center/partial-credit/**` —
  landing with filters + status counters + xlsx export, review
  detail with per-line / per-section / global decisions, Complete
  Review modal, status configuration page, email-template editor
- Sales-rep surface: `/api/v1/salesrep/partial-credit/**` — Submit
  on behalf modal + endpoints (`SalesRep` role; permissive scoping
  in Phase 1)
- Status lifecycle: `DRAFT → PENDING_APPROVAL → UNDER_REVIEW →
  APPROVED | DECLINED`; photos + edits freeze at the terminal state
- Event: `AdminCreditRequestService.completeReview` publishes
  `ReviewCompletedEvent(requestId, outcome, reviewerUserId,
  occurredAt)`
- Listener: `listener/partialcredit/ReviewCompletedEmailListener` —
  `@TransactionalEventListener(AFTER_COMMIT)` + `@Async(EMAIL_EXECUTOR)`;
  renders via `EmailTemplateService` from DB-backed templates, writes
  one `email_audit` row per recipient
- Gated by `partial-credit.review-completed-email.enabled` (default
  `true` from Sprint 4 chunk 8; env override
  `PARTIAL_CREDIT_REVIEW_EMAIL_ENABLED` remains for dev/staging)
- Snowflake sync: read-only — `VW_SALE_ORDER_SHIPMENT` denormalises the
  order manifest into `credit_requests.party_name` /
  `order_created_date` / `order_shipped_date` at draft creation
- Business-logic guide: `docs/business-logic/partial-credit.md`
- Phase 2 deferred items: automated Prolog encumbrance check, RMA
  auto-creation for accepted encumbered lines, Oracle write-back,
  S3-backed photos, `PartialCredit_*` role-tier promotion, email
  template versioning

## Unified Email Management (admin surface complete — Tasks 7-9; Task 10 frontend wiring + Task 11 Partial Credit migration still open)
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
