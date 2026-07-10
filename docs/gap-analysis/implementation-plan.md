# Gap Analysis — Implementation Plan

**Date:** 2026-07-10 · Source: [`gap-summary.md`](gap-summary.md) + [`gap-details.md`](gap-details.md) · Open decisions: [`open-questions.md`](open-questions.md)

Sequenced by **dependency**: two data-correctness fixes first (cheap, prevent bad data), then the **cross-cutting backbones** (email, Snowflake push, Oracle write-back) that ~20 downstream features need, then per-capability completions, then reporting, then the two **decision-gated** large builds (SharePoint, Device Allocation) that should not start until a human answers "is this still needed?" (see open-questions).

Effort key: **S** ≈ ≤1 day · **M** ≈ 2–5 days · **L** ≈ 1–2+ weeks.

---

## ⭐ Recommended next 5 things to build

1. **Email delivery backbone** (Phase 1 · M) — wire a real `EmailSender` (SMTP/SES) behind the existing interface + the post-commit event→listener pattern already used by partial-credit. **This one foundation unblocks ~10 missing notification features** across Auctions, PWS, RMA, CreditRequests, and BuyersUsers. Highest leverage in the whole plan.
2. **Two data-correctness fixes** (Phase 0 · S + M) — PO **week-range overlap guard** (`VAL_WeekRange_PO`; *verified* absent — prevents double-counting the PO floor into 4C target-price) and RMA **submit-time device/OfferItem validation** (`VAL_RMARequestFile`; makes `deviceId`/SKUs/sales-totals actually populate). Small, high-value, no dependencies.
3. **Make RMA functional: Oracle write-back + Deposco polling** (Phase 2 · M–L) — complete-review → create the Oracle RMA (`SUB_RMA_SendRMAToOracle`), approval email, and `ACT_UpdateRMAFromDeposco` status sync. The `Rma.oracle_*` columns and `OracleOrderClient` already exist; today the module is a shell.
4. **Finish Snowflake push-sync** (Phase 1 · M) — implement the `OfferService:671` TODO (`SUB_Offer_UpdateSnowflake`), restore the bid-submit warehouse push (`SUB_SendBidDataToSnowflake`), and the user/login/sales-rep syncs, reusing the proven `Jdbc*SnowflakeWriter` + event-listener pattern. *(Gate on open-question #5: confirm which of these are still wanted vs. deliberately dropped.)*
5. **Buyer Award Summary report** (Phase 3 · M) — the single most-cited missing report (also flagged in the repo's own auction gap doc); and immediately **remove or wire the dead `/cohort-mapping` launcher link** (*verified* dead) so the admin UI stops linking to a 404.

---

## Phase 0 — Data-correctness fixes (do first; independent, small)

### 0.1 · PO week-range overlap guard — **S**
- **Scope:** In `PurchaseOrderValidator`, on create/update, reject a week range that overlaps an existing PO's range for the same scope (the legacy `WeekPeriod`/`VAL_WeekRange_PO` rule "no two POs cover the same week").
- **Spec nodes:** `VAL_WeekRange_PO`.
- **Risks/unknowns:** legacy scoped overlap by `WeekPeriod` (dropped in V80) — decide the modern scope (global vs per-buyer-code). Need an index/query for existing overlapping ranges.
- **Acceptance:** creating/updating a PO whose `[weekFrom,weekTo]` intersects an existing PO's range returns a validation error; 4C never sees two PO floors for one week (add a repository IT).

### 0.2 · RMA submit device/OfferItem validation — **M**
- **Scope:** In RMA submit, require each uploaded IMEI/serial to match a shipped `OfferItem` for the buyer code; populate `deviceId`, SKU, and sale price from it (as `VAL_RMARequestFile` does). Reject unmatched rows.
- **Spec nodes:** `VAL_RMARequestFile`, `SUB_RMA_*` row builders.
- **Risks:** requires the OfferItem/shipment lookup source (local `pws.offer_items` vs Snowflake `VW_SALE_ORDER_SHIPMENT`). Confirm which the modern RMA should read.
- **Acceptance:** an RMA line with no matching buyer OfferItem is rejected; accepted lines carry `deviceId`/SKU/sales-total; detail roll-ups are non-zero (service test + IT).

### 0.3 · Doc/state reconciliation — **S**
- **Scope:** `data-model.md` says PO lifecycle is DRAFT/ACTIVE/CLOSED but the code is ACTIVE/CLOSED (`PurchaseOrderLifecycleState`). Fix the doc (or restore DRAFT if intended). Same pass: confirm the CreditRequests barcode-reconciliation Snowflake reader is real vs stubbed in staging.
- **Acceptance:** docs match code; no "3-state" claim remains.

---

## Phase 1 — Cross-cutting backbones (unblock ~20 downstream features)

### 1.1 · Email delivery infrastructure — **M** *(the keystone)*
- **Scope:** Provide a production `EmailSender` (SMTP/SES) behind the existing `EmailSender` interface (today only `LoggingEmailSender`), reusing the DB `EmailTemplate` + `EmailAudit` and the `@TransactionalEventListener(AFTER_COMMIT)` + `@Async` pattern proven in `ReviewCompletedEmailListener`. Add a per-flow feature flag (like `partial-credit.review-completed-email.enabled`).
- **Spec nodes (unblocks):** `SUB_SendCreditRequestSubmittedEmail`, `SUB_SendEmail_RMAApproved`, `ACT_Round3_StartNotification`, `SUB_SendSubmitBidConfirmationEmail`, `SUB_SendCounterOfferReminderEmail`, `SUB_SendManualQualificationEmail`, accounting-notification emails, inventory-notification emails.
- **Risks:** SMTP creds/secret management (tie into the deferred `JWT_SECRET`/secrets work); rendering parity; bounce handling.
- **Acceptance:** one real email (e.g., CR submit-confirmation) sends end-to-end in a non-prod env with an `email_audit` row; flag defaults off in prod until copy is reviewed.

### 1.2 · Snowflake push-sync completion — **M** *(gate on open-question #5)*
- **Scope:** Fill the stubbed/dropped warehouse pushes using the existing `Jdbc*SnowflakeWriter` + `Logging*` + event-listener pattern: offers (`OfferService:671` TODO), bid-data submit (`SUB_SendBidDataToSnowflake`), users/logins (`SUB_SendUserToSnowflake`/`SUB_SendUserLoginToSnowflake`), sales-reps (`NAN_SalesRepresentative_SynchronizeToSnowflake`), qualification overrides.
- **Spec nodes:** the above.
- **Risks:** **decision required** — several of these may have been intentionally dropped (the app re-platformed some "publish" flows to Snowflake and dropped others). Confirm the target tables exist. Don't build syncs nobody consumes.
- **Acceptance:** per confirmed flow, a submit/change emits a push (logging writer in dev, jdbc in prod) with a sync-log row.

### 1.3 · Oracle write-back extension (shared by RMA + PWS resubmit) — **M**
- **Scope:** Generalize `OracleOrderClient` so RMA complete-review can create an Oracle RMA (`SUB_RMA_PrepareContentAndSendToOracle`/`SUB_RMA_SendRMAToOracle`), and add order/RMA **resubmit** endpoints (`ACT_Order_ReSubmitToOracle`, `ACT_RMA_ReSubmitToOracle`) for failed/pending records. Write the `Rma.oracle_*` columns.
- **Spec nodes:** `SUB_RMA_SendRMAToOracle`, `CWS_PostCreateRMA`, `ACT_Order_ReSubmitToOracle`, `ACT_RMA_ReSubmitToOracle`.
- **Risks:** Oracle CWS endpoint/creds for the RMA path; the **Oracle-toggle-off simulated-success divergence** (open-question #3) must be resolved so "off" doesn't mark records shipped.
- **Acceptance:** completing an RMA writes `oracle_*` and (flagged) posts to Oracle; a failed order/RMA can be resubmitted.

---

## Phase 2 — Per-capability completions (depend on Phase 1)

### 2.1 · RMA functional completion — **M** (needs 1.1, 1.3)
- Complete-review side-effects: Oracle create + approval email + Snowflake sync; **Deposco status polling** (`ACT_UpdateRMAFromDeposco`/`SUB_SyncRMAStatus`) so RMAs auto-advance to `Received`.
- **Acceptance:** end-to-end RMA (submit→review→complete) produces Oracle record + email + status progression.

### 2.2 · Auction post-submit side-effects + notifications — **M** (needs 1.1, 1.2)
- Restore the dropped `ACT_SubmitBidData` side-effects (Snowflake push + confirmation email); wire the three `SchedulingAuction.*_notification_sent` columns via `ACT_Round3_StartNotification` + inventory-notification jobs (`Scheduled_event_Inventory_Notification`).
- **Acceptance:** submitting bids pushes to Snowflake + emails the bidder; R3-start fires one notification per buyer and stamps the sent flag.

### 2.3 · PWS automation + recovery levers — **M** (needs 1.1, 1.3)
- Counter-offer reminder job (`ACT_SendCounterOfferReminderEmails`, thresholds already in `pws_constants`); SLA-tag **scheduled** job (`batch:SE_SetSLATag` → `@Scheduled`+ShedLock wrapping the existing `PWSAdminController.setSLATags`); bulk change-order/offer-status tool (`ACT_ChangeOfferStatus_Proceed`).
- **Acceptance:** idle counters get a reminder; overdue offers auto-tag; a date-range bulk status change works.

### 2.4 · Buyers & Users management tail — **M** (needs 1.1, 1.2)
- Sales-rep **CRUD** (`Act_SaveSaleRep`/`ACT_DeleteSalesRep`) + sync; restore manual-qualification-override side-effects (round-status guard, `SUB_CreateBidDataForAllAE` seeding, Snowflake sync, `SUB_SendManualQualificationEmail`); user activation (`ACT_ActivateNewUser`) + grantable-roles gating + compliance audit (`SUB_LogBuyerCodeTypeChange_Compliance`). *(Public signup `Step1_SignupEnterInfo` — gate on open-question #9.)*
- **Acceptance:** a sales rep can be created/edited/deleted; toggling `included` re-seeds bid-data + emails + syncs.

### 2.5 · Credit Requests remaining — **S–M** (needs 1.1)
- Buyer submission-confirmation email (`SUB_SendCreditRequestSubmittedEmail`) + accounting-notification email; draft deletion; per-request Excel packet. *(Prolog encumbrance automation stays Phase-2/deferred — confirmed.)*
- **Acceptance:** submitting a CR emails the buyer; a draft can be deleted.

---

## Phase 3 — Reporting layer (mostly independent)

### 3.1 · Buyer Award Summary report — **M**
- Backend endpoint + admin page for the R3 award summary (`EcoATM_Reports` Buyer Award family). Remove/redirect the dead `/cohort-mapping` launcher link in the same PR.
- **Acceptance:** an admin can view/export the award summary; no dead launcher links remain.

### 3.2 · Buyer Bid Summary/Detail + Cohort Mapping / EB Calibration — **L**
- The other two legacy report families. Cohort Mapping / EB Calibration ties to reserve-bid calibration; Buyer Bid Summary/Detail is per-buyer bid analytics.
- **Risks:** decide data source (local vs Snowflake); confirm these reports are still used before building (open-question #10).
- **Acceptance:** each report renders with parity data + export.

---

## Phase 4 — Decision-gated large builds (DO NOT start without a human answer)

### 4.1 · SharePoint / MS-Graph "AllBids export" — **L** *(gate: open-question #1)*
- The entire Integration capability (SharePoint drive upload + AllBids Excel builders + MS-Graph OAuth/subscription). **Confirmed absent.** But "publish all bids" was **re-platformed to Snowflake** (`BidExportService`). **Decision:** is the Snowflake path the accepted replacement (→ mark SharePoint obsolete, delete the orphaned `sharepoint_method_config` columns) or must SharePoint be restored (→ build an MS-Graph client: OAuth token, drive upload, 32 grade-file builders)? Do not build until answered.

### 4.2 · Device Allocation module — **L–XL** *(gate: open-question #2)*
- The entire `EcoATM_DA` domain: R3 winner-award review/accept/finalize, external-DB pull (`SUB_GetDADataFromExternalDB`), awarded-qty assignment, SharePoint transfer, batch `SCE_LoadDAData`. **0 files in the repo.** This is a full new capability. **Decision:** is post-auction device allocation still an operated business process, or superseded? If in scope, this is its own multi-sprint effort with its own data model.

---

## Explicitly NOT recommended (obsolete — see gap-details "Likely-dead" sections)
The `(none)` bucket (vendored Mendix marketplace plumbing — already replaced by Spring), all 11 `Disabled` legacy batches, QA/purge/backup-restore tooling (`DeleteAll_PO`, `ACT_WeeklyPODELETE`), the `POHelper`/`WeeklyPO`/`PurchaseOrderDoc` client-state scaffolding, inbound Oracle config/error-XML push, and the record-lock (`EcoATM_Lock`)/idle-timeout popups (superseded by JWT + ownership guards). Porting these would add dead code.
