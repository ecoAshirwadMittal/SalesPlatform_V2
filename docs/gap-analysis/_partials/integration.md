# Integration — gap analysis

**Rollup:** Implemented 0 · Partial 1 · Missing 8 · Divergent 0 (of 9 assessed behavior-groups, +1 obsolete) · spec surface: 1 page / 0 batches / 36 reachable flows (50 total)

## State (2-3 sentences)
The entire Integration capability is a single legacy behavior: the `EcoATM_Direct_Sharepoint` module's "AllBids by BuyerCode" Excel-generation-and-SharePoint-upload pipeline (plus a "ZeroQtyCap" variant), built on Microsoft Graph OAuth + the Mendix `Sharepoint`/`XLSReport`/`OQL` marketplace connectors. **None of it is ported** — the modern backend has no SharePoint or Microsoft Graph client at all (grep for `sharepoint|microsoftgraph|CreateDriveItem|GetDrives|webhook|/subscriptions|documentgeneration` in `backend/src/main/java` returns only schema-column names, never client code). The legacy intent "publish every buyer's bids to a downstream system" is instead served, divergently, by the modern Snowflake push path (`BidExportService` / `AuctionSnowflakeResyncService`), so the data leaves the app — just not as an Excel file on a SharePoint drive.

## Entry points & flows
| Spec node (id · name · kind) | Verdict | Modern evidence (path/symbol) | Behavior gap / notes |
|---|---|---|---|
| `EcoATM_Direct_Sharepoint.ACT_CreateDriveItemCreate` · SharePoint upload primitive · flow | **MISSING** | not found (searched backend/src for `CreateDriveItem`, `GetDrives`, `driveitem`, `Sharepoint.*`) — confirmed-absent | The shared "push a FileDocument into the Documents drive, gated by `SharepointBidRoundEnabled`" primitive has no modern equivalent. |
| `EcoATM_Direct_Sharepoint.ACT_GetAuthorization` · Graph OAuth token lookup · flow | **MISSING** | not found (searched `MicrosoftGraph`, `Authorization`, `accessToken`, `refreshToken`, OAuth) — confirmed-absent | No Graph OAuth/authorization record or token-refresh in the modern app. |
| `EcoATM_Direct_Sharepoint.SUB_createSharepointFile_OQLQuery` · OQL AllBids build+export · flow | **MISSING** | not found — confirmed-absent | OQL-bulk build → `XLSReport.GenerateExcelDoc` → upload not reproduced. |
| `EcoATM_Direct_Sharepoint.SUB_createSharepointFile_OLD` / `SUB_CreateBidDataDownload_NonDW` / `_DW` · legacy per-entity AllBids build · flow | **MISSING** | not found — confirmed-absent | Row-by-row `AllBidDownload` build for DW + non-DW tracks not reproduced. |
| 32× `SUB_AllBidDownload_Create_*` / `SUB_Change_ExistingDevice_*` (A–H grade truth table, DW + non-DW) · flow | **MISSING** | not found (searched `AllBidDownload`, `allbid`) — confirmed-absent | The 8-way (BidData/Quantity/Amount/Qty>0) × create/update × DW/non-DW helper matrix has no modern port; `AuctionUI.AllBidDownload` entity is not migrated. |
| `EcoATM_Direct_Sharepoint.SUB_SendZeroQtyCapFileToSharepoint` / `_allBuyerCodes` / `ACT_SendZeroQtyCapFileToSharepoint` · ZeroQtyCap export+upload · flow | **MISSING** | not found — confirmed-absent | Per-buyer + bulk "your quota dropped to zero" Excel alert to SharePoint not reproduced. |
| `EcoATM_Direct_Sharepoint.ACT_TransferBuyerCodeBidsToSharepoint` · per-submit flag-gated push · flow | **MISSING** | `AuctionsFeatureConfig.sendFilesToSharepointOnSubmit` column migrated (V8/V18) but **no consumer** | The flag that would trigger a per-submit SharePoint push exists in schema; nothing reads it. |
| `EcoATM_Direct_Sharepoint.SUB_GetSharepointTemplate` + `AuctionUI.SharePointMethod` (OQL vs OLD) · template/method switch · flow | **PARTIAL** | `auctions.sharepoint_method_config` (`use_api_upload`, `oql_endpoint`, `api_endpoint`) migrated (V59/V63); `bid_submit_log` has `Push_To_Sharepoint` action; `sp_retry_count` column | **Config/state schema migrated, zero runtime consumer.** Data model preserves the OQL-vs-API toggle and retry counter but no code uploads or switches on it. |
| `EcoATM_Direct_Sharepoint.DS_GetParticipatedBuyerCodes` · participated-buyer-codes query · flow | **MISSING** | not found — confirmed-absent | The "buyers who placed a non-zero bid" set (input to the bulk ZeroQtyCap job) is not reproduced for this purpose. |
| `EcoATM_Direct_Sharepoint.BidDataUploadPOC` (page) + `Test_UploadBidDataDoc` / `Test_GetBidDataDoc` · flow/page | **Obsolete — don't port** | n/a | Developer POC/diagnostic surface; explicitly "not a production end-user page." |

## Biggest gaps (named, with spec node ids)
1. **No SharePoint / Microsoft Graph client whatsoever** — the whole capability (`ACT_CreateDriveItemCreate`, `ACT_GetAuthorization`, `Sharepoint.CreateDriveItem`/`GetDrives`) is confirmed-absent. Any requirement to deliver bid workbooks to a SharePoint drive is unmet.
2. **AllBids-by-BuyerCode Excel export** (`SUB_createSharepointFile_OQLQuery`/`_OLD`, `SUB_CreateBidDataDownload_NonDW`/`_DW`, 32 `AllBidDownload` helpers) — the entire per-buyer spreadsheet generator is gone; `AuctionUI.AllBidDownload` was never migrated.
3. **ZeroQtyCap buyer-alert files** (`SUB_SendZeroQtyCapFileToSharepoint*`) — no modern path notifies a buyer their allowed quantity was capped to zero via a file.
4. **Config migrated without a consumer** (`sharepoint_method_config`, `send_files_to_sharepoint_on_submit`, `sp_retry_count`, `bid_submit_log.Push_To_Sharepoint`) — dead configuration that will mislead unless either wired up or documented as intentionally dropped.

## Net-new modern behavior (not in legacy)
- **Snowflake bid publication replaces SharePoint export (divergent re-platforming of the same intent).** `service/auctions/biddata/BidExportService`, `controller/admin/AuctionSnowflakeResyncAdminController` + `service/auctions/snowflake/AuctionSnowflakeResyncService` explicitly "Port Mendix `ACT_Auction_SendAllBidsToSnowflake_Admin`" — all-bids data now flows to Snowflake (`Jdbc*SnowflakeWriter`), not to an Excel file on a drive.
- **Broad modern integration layer for other capabilities** (not this one's behavior): `service/snowflake` + `service/auctions/snowflake` (Jdbc/Logging writers+readers for auction-status, bid-ranking, target-price, reserve-bid, PO, aggregated-inventory, credit-request, buyer), `service/email` (`EmailSender`/`SmtpEmailSender`/`LoggingEmailSender` + templates), `OracleOrderClient`/`OracleConfigController`, Deposco (`AtpSyncController`/`AtpSyncService`/`DeposcoInventoryDto`).

## Likely-dead / obsolete legacy (don't port)
- `BidDataUploadPOC` page + `Test_UploadBidDataDoc` / `Test_GetBidDataDoc` — developer POC/diagnostics.
- The Microsoft Graph **change-notification subscription** machinery behind SharePoint (`MicrosoftGraph.SUB_Subscription_*`, `SCE_Subscription_Renew` daily job, `SE_AccessToken_Refresh` — the latter already **Disabled** in legacy) — these are marketplace-connector plumbing (see the (none) bucket), obsolete unless SharePoint integration is deliberately revived.

### SharePoint / REST / access-token verdict (explicit)
- **SharePoint drive upload** (`Sharepoint.CreateDriveItem` / `GetDrives`, `ACT_CreateDriveItemCreate`): **CONFIRMED ABSENT** in the modern repo (grep of `backend/src/main/java` for `sharepoint|CreateDriveItem|GetDrives|driveitem` matches only migration-SQL column names, never client code).
- **Microsoft Graph REST + OAuth access token** (`MicrosoftGraph` verbs, `ACT_GetAuthorization`, subscription renewal): **CONFIRMED ABSENT** (grep for `microsoftgraph|graph.microsoft|webhook|subscription.?renew|change.?notification|/subscriptions|OAuth|accessToken|refreshToken` in backend Java is empty).
- **DocumentGeneration access-token refresh** (`SE_AccessToken_Refresh`): **CONFIRMED ABSENT** and moot — the legacy job is Disabled and never runs.
