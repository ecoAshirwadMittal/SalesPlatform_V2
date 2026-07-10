# Gap Analysis — Open Questions & Decisions

**Date:** 2026-07-10 · Companion to [`gap-summary.md`](gap-summary.md) / [`gap-details.md`](gap-details.md) / [`implementation-plan.md`](implementation-plan.md).

These need a **human product/architecture decision** before the related build starts. Numbered so the plan can reference them (e.g. Phase 4.1 gates on #1).

## A. Big "still needed?" decisions (block large builds)

**#1 — SharePoint export vs. Snowflake re-platform.** The entire **Integration** capability — the "AllBids by BuyerCode" Excel-generate-and-upload-to-SharePoint pipeline (`ACT_SendBidstoSharepoint_perBuyerCode_Admin`, MS-Graph OAuth/subscription) — is **confirmed absent**, but "publish all bids" was re-implemented against **Snowflake** (`BidExportService`, `AuctionSnowflakeResyncService`). *Is the Snowflake path the accepted replacement?* If **yes** → mark SharePoint obsolete and **delete the orphaned config columns** (`sharepoint_method_config`, `send_files_to_sharepoint_on_submit`, `sp_retry_count`, `bid_submit_log.Push_To_Sharepoint`) that were migrated with no consumer. If **no** → Phase 4.1 (build an MS-Graph client) is on. *Recommendation: Snowflake replaces it; retire SharePoint.*

**#2 — Device Allocation module: rebuild or retire?** The whole `EcoATM_DA` domain (R3 winner-award review/accept/finalize, external-DB pull, awarded-qty, SharePoint transfer, batch `SCE_LoadDAData`) has **0 files** in the repo. Its legacy batch `SCE_LoadDAData` is `Disabled`. *Is post-auction device allocation still an operated process?* This is a multi-sprint build if in scope. **Needs a business owner to confirm before any estimate is committed.**

**#3 — Oracle-toggle-off behavior (correctness, not just a gap).** Legacy returns a generic **error** when Oracle is off (offer → `Pending_Order`); the modern `OracleOrderClient` returns **simulated success** `returnCode="00"` (offer → `Ordered`). *Is the simulated-success a deliberate dev convenience, or a latent bug that would mark real orders shipped if the toggle is ever off in prod?* Resolve before Phase 1.3 (Oracle write-back), since RMA will reuse the same client.

## B. Scope confirmations (shape a phase, don't block it)

**#4 — Which Snowflake syncs are actually wanted?** Several "sync to Snowflake" flows are stubbed/dropped (offer, bid-data-submit, user, login, sales-rep, qualification-override). Some look intentionally dropped (re-platformed), others look like unfinished TODOs (`OfferService:671`). *Confirm the per-entity list of syncs to build in Phase 1.2 — don't build warehouse pushes nothing consumes.*

**#5 — RMA / offer read source for validation.** RMA submit validation (`VAL_RMARequestFile`) and offer flows need the shipped-item lookup. *Read from local `pws.offer_items` or the Snowflake `VW_SALE_ORDER_SHIPMENT` view?* (CreditRequests already reads the latter for the manifest.) Affects Phase 0.2.

**#6 — Public self-service signup.** Legacy has `Step1_SignupEnterInfo` / `ACT_ActivateNewUser`. *Is buyer self-registration in scope, or is the modern app admin-provisioned only?* Affects Phase 2.4.

**#7 — Which "Disabled" legacy jobs to revive.** 11 of 15 legacy batches are `Disabled` (inventory-notification, LoadDAData, ScheduleAuctionStatus, UpdateDeviceInventory, GetAggregatedInventory, PO-PackOut-weekly, log/doc/token cleanup, IdP-metadata sync, subscription-renew). The plan assumes most stay retired. *Confirm which enabled behaviors are genuinely needed* (e.g., counter-offer reminders + SLA tagging are recommended; inventory-notification emails depend on #6-style product intent).

**#8 — Reports still in use?** Buyer Bid Summary/Detail and Cohort Mapping / EB Calibration are fully missing. *Are all three legacy report families still used operationally, or has reporting moved to Snowflake/BI?* Affects Phase 3.2 (and whether to build vs. mark obsolete). Buyer Award Summary (Phase 3.1) is separately confirmed-wanted (repo's own docs flag it).

## C. Confirmed likely-dead / obsolete legacy — **do NOT port**
(From the "Likely-dead" sections of each capability partial; listed so nobody rebuilds them.)
- **The `(none)` bucket** — 74/902 reachable; all reachable nodes are vendored Mendix marketplace modules (MicrosoftGraph, ForgotPassword, TaskQueueScheduler, SAML20, Email_Connector, Encryption, Excel-import). Replaced by Spring Security SAML / Spring Mail / `@Scheduled`+ShedLock / JWT-crypto.
- **QA / destructive utilities** — `DeleteAll_PO`, `ACT_WeeklyPODELETE` (hardcoded to ProductID 16687/BuyerCode 'ADPO'), `ACT_DeleteDA`, backup/restore/purge tooling.
- **Mendix client-state scaffolding** — `POHelper`/`POHelper_Account` staging singletons, `PurchaseOrderDoc`/blob containers, spinner-wrapper nav flows, stub `DS_Get*` data sources (return blank). React + REST replace these.
- **Dropped-by-design entities** — `WeeklyPO`/pack-out fulfillment tracker (V80 "4C unused"), `WeekPeriod` (but note this dropped the overlap guard — see Phase 0.1), inbound Oracle config/error-XML push (`SUB_Oracle_Configuration`).
- **Record-lock / idle-timeout UX** — `EcoATM_Lock` record locks + multi-tab keep-alive popups; superseded by JWT expiry + `PwsOwnershipGuard` (a deliberate divergence, not a gap — unless the read-only "someone else is editing" bounce is a required UX, which is worth a product check).

## D. Known doc/code inconsistencies to fix (cheap)
- **PO lifecycle** — `data-model.md` says DRAFT/ACTIVE/CLOSED; code is ACTIVE/CLOSED only (`PurchaseOrderLifecycleState`). Fix the doc (Phase 0.3).
- **Dead `/cohort-mapping` link** — `admin/auctions-data-center/page.tsx:45` links to a route that doesn't exist (*verified*). Remove or build (Phase 3.1).
- **Barcode reconciliation (CreditRequests)** — logic complete but the live JDBC Snowflake reader is stubbed by default and "unproven in staging" per the module notes; validate against a real reader before relying on it.
- **Orphaned SharePoint config columns** — migrated but consumed by nothing (ties to #1).
