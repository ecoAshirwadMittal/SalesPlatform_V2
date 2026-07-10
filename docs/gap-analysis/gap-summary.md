# Gap Analysis — Summary

**Date:** 2026-07-10
**Ground truth:** `../Rebuilding/rebuild_graph.json` (behavioral graph of the legacy Mendix app — 2,336 nodes / 3,298 edges).
**Method:** compared this repo's actual controllers/services/models/migrations/routes against the graph's **reachable** surface (dead legacy flows excluded), capability by capability, judging behavior via decisions/reads/writes/integrations. See [`gap-details.md`](gap-details.md) for per-node evidence and [`implementation-plan.md`](implementation-plan.md) for the build plan.

## Headline

Across **247 significant entry-points/flows** assessed (reachable pages, batches, and load-bearing flows — not the ~1,140 unreachable helper flows):

| Verdict | Count | Share |
|---|---:|---:|
| **IMPLEMENTED** | 123 | 50% |
| **PARTIAL** | 34 | 14% |
| **MISSING** | 76 | 31% |
| **DIVERGENT** | 14 | 6% |

**The modern app has faithfully rebuilt the transactional core** (auction lifecycle, offer→order, partial-credit, PO authoring) at ~50% behavioral parity on the significant surface. **The ~45% that is partial-or-missing is not random — it clusters into five cross-cutting "backbone" gaps** plus one absent module and the reporting layer:

1. **Email / notification delivery** — real SMTP is not wired (`LoggingEmailSender` is the default); ~10 notification flows across every capability are unbuilt (auction inventory/R3-start/bid-confirm, counter-offer reminders, credit-request submit + accounting, RMA approved/submit, manual-qualification).
2. **Snowflake push-sync** — several "sync to warehouse" flows are stubbed or dropped (`OfferService:671` TODO, bid-data submit push, user/login sync, sales-rep sync, RMA sync, qualification-override sync).
3. **Oracle write-back** — RMA complete-review never creates the Oracle RMA (the `Rma.oracle_*` columns are scaffolded but never written); no order/RMA resubmit.
4. **SharePoint / MS-Graph** — **confirmed absent** (grep-empty in backend Java); the entire "AllBids export" Integration capability was re-platformed to Snowflake instead (a divergence needing a keep/restore decision).
5. **Deposco status polling** — no RMA-status sync, so RMAs never auto-advance to `Received`.

Plus: the **Device Allocation module is entirely absent** (a whole R3 winner-award domain), the **Reports layer is essentially unbuilt** (all 3 legacy report families missing; the admin launcher even links to a dead `/cohort-mapping` route), and two **data-correctness gaps** (PO week-range overlap → PO-floor double-count into 4C; RMA submit skips device/OfferItem validation → zeroed roll-ups).

## Per-capability table

| Capability | Impl | Part | Miss | Div | Assessed | State (2–3 sentences) — biggest gaps |
|---|---:|---:|---:|---:|---:|---|
| **Auctions** | 33 | 12 | 18 | 2 | 65 | Core lifecycle fully rebuilt (create→schedule→event-driven rounds→R1→4C ranking/target-price→R2→R3 + reserve-bids + bidder dashboard). **Biggest gaps:** the entire **Device Allocation** module (R3 winner award/accept/finalize + external-DB pull) is absent; **all buyer email notifications** are unbuilt (the `*_notification_sent` columns exist but nothing writes them); **SharePoint bid export** and the post-submit side-effects (Snowflake push, Excel/SharePoint, confirm email) were dropped from `BidDataSubmissionService.submit`. |
| **PWS** | 33 | 6 | 12 | 4 | 55 | The most faithfully ported surface: cart→offer→counter→order with the **real Oracle Create-Order** integration, sales review, inventory reservation, order history, pricing, MDM CRUD, and **real Deposco ATP sync**. **Biggest gaps:** offer→Snowflake sync is a literal TODO (`OfferService:671`); counter-offer reminder emails, resubmit-to-Oracle, and a bulk status-change tool are missing; SLA tagging is a manual admin button, not the every-minute job. **Divergence:** Oracle-toggle-off *simulates success* vs the legacy error path. |
| **Buyers & Users** | 12 | 5 | 13 | 2 | 32 | Buyer/code/qualification read + core CRUD present, but the management + governance tail is thin. **Biggest gaps:** **sales-rep management is read-only** (no create/edit/delete/sync); **manual qualification override drops its side-effects** (round-status guard, `SUB_CreateBidDataForAllAE` seeding, Snowflake sync, email); user lifecycle (activation, public signup, user→Snowflake, grantable-roles gating, compliance audit) is absent. `/sso` is a hardcoded stub. |
| **RMA** | 11 | 5 | 11 | 2 | 29 | **A shell of the legacy module.** Submit + per-line review + status/template config exist, but **complete-review has no downstream effects** — no Oracle create, approval email, Snowflake sync, or resubmit (`oracle_*` columns never written); **submit skips the device/OfferItem validation** (`VAL_RMARequestFile`) so `deviceId`/SKUs/sales-totals never populate and roll-ups read 0; **Deposco status polling is absent** (RMAs never auto-advance to `Received`). |
| **Credit Requests** | 22 | 1 | 6 | 1 | 30 | **The most complete module** (Phase 1 done, 124/124 backend tests): 5-step wizard, all three reason branches, admin per-line/section/global review with live totals, complete-review + async buyer email, status config, template editor, photos, xlsx export, sales-rep on-behalf. **Biggest gaps:** the **KPI/Reports dashboard** is absent; **no buyer submission-confirmation email** (only review emails); Prolog encumbrance automation is a deferred Phase-2 item. |
| **Purchase Orders** | 10 | 2 | 2 | 1 | 15 | The authoring half is cleanly rebuilt (create/edit, week ranges, Excel create+replace import, buyer-code validation, export, one-way Snowflake push) and 4C correctly consumes `po_detail` as a `GREATEST(...)` floor. **Biggest gaps:** the **week-range overlap guard is not enforced** (`VAL_WeekRange_PO`) — *verified*: modern only checks `from ≤ to`, so two POs can cover the same week and double-count the floor; fulfillment/pack-out reconciliation was intentionally dropped. |
| **Integration** | 0 | 1 | 8 | 0 | 9 | This capability *is* the SharePoint "AllBids by BuyerCode" Excel-generate-and-upload pipeline — and it is **entirely unported**. **Confirmed absent:** SharePoint drive upload, MS-Graph REST + OAuth token/subscription renewal, the AllBids Excel builders, ZeroQtyCap alerts. Config columns (`sharepoint_method_config`, `send_files_to_sharepoint_on_submit`) were migrated with **no consumer**. "Publish all bids" was re-platformed to Snowflake (a divergence — see open questions). |
| **Platform** | 2 | 2 | 2 | 2 | 8 | `PWSAdminController` is a faithful, broad "PWS Control Center" (feature flags ✅, maintenance mode ✅, error messages, constants, order/RMA status, nav-menu, ranks). **Gaps:** SLA tagging works but the every-minute job is absent (PARTIAL); the `EcoATM_Lock` record-lock framework and multi-tab idle-timeout are missing (ShedLock is job leader-election, not record locks; JWT expiry replaces idle-timeout). **Divergent:** custom error-log/perf-timer → slf4j + audit tables. |
| **Reports** | 0 | 0 | 4 | 0 | 4 | **Essentially unbuilt.** All three legacy report families — **Buyer Award Summary**, **Cohort Mapping / EB Calibration**, **Buyer Bid Summary/Detail** — have no backend endpoints and no pages, and the admin launcher links to a **dead `/cohort-mapping` route** (*verified*). Modern reporting (`Round3ReportController`, `BidDataAdminController`, `BuyerOverviewController`) is net-new and different data. Matches the repo's own "Buyer Award Summary outstanding" note. |
| **(none)** | — | — | — | — | 902 | **Obsolete library code — do not port.** Only 74/902 (~8%) are reachable, and every reachable node is vendored Mendix marketplace plumbing (MicrosoftGraph, ForgotPassword, TaskQueueScheduler, SAML20, Email_Connector, Encryption, Excel-import). The modern app already replaces these with Spring Security SAML, Spring Mail, `@Scheduled`+ShedLock, JWT/crypto. |

## Net-new modern behavior (not in legacy)
The rebuild is not a pure port — it adds: the full **security hardening** layer (buyer-code ownership scoping, JWT-derived identity, auth + upload rate-limiting — no legacy equivalent), **event-driven Snowflake push** with pluggable `logging`/`jdbc` writers, a consolidated **19→5 admin-screen** PWS Control Center, audited soft-delete, and the async **partial-credit** module's review/email/photo pipeline.

## Where to start
See [`implementation-plan.md`](implementation-plan.md). In one line: **build the email backbone first** (it unblocks ~10 features across every capability), fix the two data-correctness gaps (PO overlap, RMA validation), make RMA actually functional (Oracle write-back + Deposco polling), finish Snowflake push-sync, and stand up the Buyer Award Summary report — then take a business decision on SharePoint-vs-Snowflake and the Device Allocation module before investing in those large builds.
