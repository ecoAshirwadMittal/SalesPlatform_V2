# PWS (Premium Wholesale) — gap analysis

**Rollup:** Implemented 33 · Partial 6 · Missing 12 · Divergent 4 (of 55 assessed) · spec surface: 63 pages / 1 batch / 289 reachable flows

## State (2-3 sentences)
The buyer offer→counter→order lifecycle is the most faithfully ported surface in the whole app: cart CRUD, submit-offer, submit-order with the real Oracle Create-Order integration (token + POST + 3-way response branch), sales line-by-line review, buyer counter-response, inventory reservation, order history/details, pricing, MDM device/master-data CRUD, and the Deposco ATP inventory sync all have strong behavioral parity backed by real endpoints and JPA models. The clear gaps cluster in three places: (1) **Snowflake offer-status sync is stubbed** (`SUB_Offer_UpdateSnowflake` is a TODO — no per-offer upsert and no manual resync page), (2) **scheduled/automation flows are manual or absent** (the every-minute `SE_SetSLATag` batch is a manual admin button; counter-offer reminder emails have config but no sender job), and (3) **ops recovery levers are missing** (`Resubmit-to-Oracle`, bulk change-order-status, Deposco order-number/shipment lookup, and buyer Excel offer upload). A handful of legacy behaviors are deliberately divergent (concurrent-edit `EcoATM_Lock` → buyer-code ownership guard; Oracle toggle-off returns simulated success instead of a pending-order error).

## Entry points, screens & flows

### Shop / cart / browse
| Spec node (id · name · kind) | Verdict | Modern evidence (path/symbol) | Behavior gap / notes |
|---|---|---|---|
| `EcoATM_PWS.PWSOrder_PE` / `PWSOrder_PE_Dashboard` (page) — buyer store browse + grid search | IMPLEMENTED | `frontend/src/app/pws/order/page.tsx`, `pws/inventory/page.tsx`; `InventoryController` `GET /api/v1/inventory/devices` (`listActiveDevices`, `listFilteredDevices` itemType/excludeGrade/minAtpQty), `PwsInventoryService` | ATP-aware device browse + filter present. |
| `EcoATM_PWS.DS_GetOrCreateOrderItem` / `_CaseLot` (flow) — add/edit cart line, case-lot aware, CSS style class | IMPLEMENTED | `OfferController` `PUT /pws/offers/cart/items` → `OfferService.upsertCartItem` (case-lot size multiply, `caseLotRepository`) | Legacy `CAL_BuyerOfferItem_CSSStyle` red/orange price-vs-list styling is presentational; modern computes client-side. |
| `EcoATM_PWS.PWS_MyOffer` (page) — cart review | IMPLEMENTED | `pws/cart/page.tsx`; `OfferController` `GET /cart`, `DELETE /cart/items/{sku}` | — |
| `EcoATM_PWS.ACT_ResetOrder` / `PWS_ResetConfirmation` (flow/page) — discard cart edits | IMPLEMENTED | `OfferController` `DELETE /pws/offers/cart` → `OfferService.resetCart` | — |
| `EcoATM_PWS.PWS_DeviceView` (page) — device detail + build-offer / similar-SKU drawer | PARTIAL | browse/add wired via store + cart; no dedicated device-detail route located | Device detail drawer + "similar SKUs" offer-builder not found as a distinct screen (searched `pws/**`, no `device-view`/`[sku]` route). |
| `EcoATM_PWS.PWS_AlmostDone` (page) — final checkout confirmation popup | PARTIAL | folded into `POST /cart/submit` (`OfferService.submitCart`) | No standalone confirmation step; single submit call. Minor. |
| `EcoATM_PWS.BuyerOffer_Step1_SelectExcelFile` / `Step2_LoadExcelFile` (page) — buyer bulk **Excel offer upload** wizard (`NAN_Buyer_UploadOfferExcel`) | MISSING | not found — buyer offer endpoints are per-item only | `PricingController /devices/upload` is admin future-price CSV, not buyer offer upload. Confirmed-absent buyer bulk-offer ingest. |

### Offers / offer submission
| Spec node | Verdict | Modern evidence | Behavior gap / notes |
|---|---|---|---|
| `EcoATM_PWS.ACT_Offer_SubmitOffer` (flow) — BuyerOffer→Offer, TotalPrice>0 filter, reserve, confirmation email | IMPLEMENTED | `OfferController` `POST /{offerId}/submit-offer` + `/cart/submit` → `OfferService.submitOffer` (Sales_Review drawer + `reserveDeviceQuantity`); `PwsOfferEmailEvent.OfferConfirmation` | Strong parity incl. TotalPrice>0 filter and post-commit email. |
| `EcoATM_PWS.ACT_Offer_SubmitOrder` (flow) — BuyerOffer→Offer+Order+Oracle, 3-way branch on ReturnCode | IMPLEMENTED | `OfferController` `POST /{offerId}/submit-order` → `OfferService.submitOrder` → `OracleOrderClient.submitOrder` → `handleOracleResponse` (no-response / `'00'` / other) | 3 outcome branches present (pending email / ordered+confirmation / failure). |
| `EcoATM_PWS.ACr_UpdateOfferID` (flow) — sequential zero-padded per-buyer-code Offer ID on save | IMPLEMENTED | `service/OfferNumberGenerator`; `V27__offer_id_sequence.sql` | — |

### Oracle order integration
| Spec node | Verdict | Modern evidence | Behavior gap / notes |
|---|---|---|---|
| `EcoATM_PWS.SUB_Order_SendOrderToOracle` + `CWS_PostToken` + `EcoATM_PWSIntegration.CWS_PostCreateOrder` — token auth + POST create-order, `IsOracleCreateOrderAPIOn` toggle | IMPLEMENTED | `service/OracleOrderClient` (`fetchOracleToken` client_credentials, `postCreateOrder` Bearer POST to `config.createOrderPath`, parses camel+Pascal keys); `OracleConfigController` `POST /admin/oracle-config/test-auth`; `model/integration/OracleConfig`, `V14` | Real HTTP client, timeout, audit logs. **Divergent toggle-off** — see Divergent table. |
| `EcoATM_PWS.SUB_Offer_PrepareOraclePayload` (flow) — build OrderRequest/OrderLineItem JSON from accepted/countered-accept/finalize items | IMPLEMENTED | `OfferService.prepareOraclePayload` (accepted-item filter, case-lot qty) | — |
| `EcoATM_PWS.SUB_CreateOrderResponse_ManageResult` (flow) — process response, pending/adjusted/standard confirmation emails | IMPLEMENTED | `OfferService.handleOracleResponse` + `PWSEmailService` (`sendOrderConfirmationEmail`, `sendPendingOrderEmail`) | Adjusted-quantity email folded into pending email — see Divergent. |
| `EcoATM_PWS.ACT_Order_ReSubmitToOracle` (flow) + `Order_detail` "Resubmit to Oracle" (page) — manual retry of a failed order | MISSING | no `resubmit`/`re-submit` endpoint (searched all controllers/services) | Confirmed-absent ops recovery lever for stuck/failed Oracle orders. |

### Counter-offers (buyer side)
| Spec node | Verdict | Modern evidence | Behavior gap / notes |
|---|---|---|---|
| `EcoATM_PWS.NAV_PWSCounterOffers` / `SUB_NavigateToCounterOffers` (flow) — route single-vs-multiple pending, acquire lock | IMPLEMENTED | `CounterOfferController` `GET /pws/counter-offers` + `/{offerId}`; `pws/counter-offers/(page|[offerId])` | Routing present. Lock behavior **divergent** (see below). |
| `EcoATM_PWS.ACT_Offer_BuyerAcceptAllCounters` (flow) — bulk accept counters, case-lot aware | IMPLEMENTED | `POST /{offerId}/accept-all` → `CounterOfferService.acceptAllCounters` (`caseLotRepository`) | — |
| `EcoATM_PWS.ACT_Offer_BuyerSubmitCounterResponse` (flow) — accept/decline → if `Ordered` create Order + send to Oracle + Snowflake | IMPLEMENTED | `POST /{offerId}/submit` → `CounterOfferService.submitCounterResponse` → `offerService.submitOrder` when ordering; else all-declined path | Order placement present; Snowflake sync stubbed (see Snowflake). |
| `EcoATM_PWS.VAL_Offer_IsCounterOfferReadyForSubmit` (flow) — gate submit until all counters answered | IMPLEMENTED | `submitCounterResponse` guard: "All countered SKUs must be either Accepted or Rejected" | — |
| `EcoATM_PWS.ACT_Offer_EditCounterOfferByBuyer` (flow) — reopen submitted counter, re-split by grade/case-lot, lock bounce | PARTIAL | `PUT /{offerId}/items/{itemId}/action` (`setBuyerItemAction`) | Per-item accept/reject present; the grade / `A_YYY` / case-lot bucket re-split and the read-only lock bounce are not reproduced. |

### Offer review (sales / admin)
| Spec node | Verdict | Modern evidence | Behavior gap / notes |
|---|---|---|---|
| `EcoATM_PWS.PWSOffers` / `PWSOffer_OfferItems` / `PWS_OfferItemView` (page) — central grid + line-by-line accept/decline/counter/finalize | IMPLEMENTED | `OfferReviewController` (summary, list, paged, `GET /{offerId}`, `PUT items/{itemId}/action`, `.../counter`, accept-all, decline-all, finalize-all, complete-review); `pws/offer-review/(page|[offerId])` | Full review workspace. |
| `EcoATM_PWS.ACT_Offer_SalesFinalizeAll` + `VAL_Offer_Finalize` (flow) — finalize whole offer, guard mid-negotiation items | IMPLEMENTED | `POST /{offerId}/finalize-all`; `OfferReviewService.finalizeAll` | — |
| `EcoATM_PWS.SUB_CalculateCounterOfferSummary` + `SUB_Offer_DefineFinalOfferStatus` (flow) — counter summary totals; Ordered-vs-Declined terminal status | IMPLEMENTED | `OfferReviewService.completeReview` (counter-item → `Buyer_Acceptance` + summary totals; none → `offerService.submitOrder`); over-ATP guard on complete | Matches "any accept → Ordered else Declined" intent. |
| `EcoATM_PWS.ACT_UpdateOfferMasterHelper_HasItems` / `ACT_ChangeOfferStatus` (flow) — per-status tab counts / highlight | IMPLEMENTED | `OfferReviewController` `GET /summary` (`getStatusSummaries`), `GET /counts` | Status-bucket counters present. |
| `EcoATM_PWS.SUB_UpdateOfferDrawerStatus` (flow) — central status state-machine: reserve qty per Device/CaseLot **and** Snowflake sync on every status change | PARTIAL | drawer statuses set in `submitOffer`/`completeReview`; `reserveDeviceQuantity` present | Inventory reservation present; the **Snowflake push half is stubbed** (see Snowflake row). |

### Inventory / devices / reservation
| Spec node | Verdict | Modern evidence | Behavior gap / notes |
|---|---|---|---|
| `EcoATM_PWS.SUB_ReserveQuantityForDevice` / `ForCaseLot` + `SUB_UpdateReservedQuanityPerDevice` (flow) — reserved = min(ordered sum, avail); atp = avail − reserved | IMPLEMENTED | `OfferService.reserveDeviceQuantity`; `AtpSyncService.updateReservedQuantities` (`sumReservedQtyByDeviceId`) | Formula matches legacy. |
| `EcoATM_PWSMDM.Device_Overview` / `Device_Edit` (page) | IMPLEMENTED | `admin/pws-data-center/devices`; `InventoryController /devices`, `PricingController /pricing/devices`; `model/mdm/Device`, `V13` | — |
| `EcoATM_PWSMDM.{Brand,Capacity,Carrier,Category,Color,Model,Grade,Note,CaseLot}_Overview/NewEdit` (page) — master-data CRUD + JSON audit-on-save | IMPLEMENTED | `admin/pws-data-center/master-data` (tabbed) → `AdminMasterDataController /admin/master-data/{type}`; `V13`, `V31` case_lots; audit → `V56 pws_admin_audit` | Consolidated 8 grids → 1 tabbed screen (per `docs/tasks/pws-data-center-port.md`); JSON-warning-log audit → audit table (divergent-minor, intentional). |
| `EcoATM_PWS.PropertiesUtility_Update` (page) — bulk device-property merge/mass-edit tool | MISSING | not found (searched controllers/services) | Likely-obsolete admin power tool; see Likely-dead. |

### Orders / history / status
| Spec node | Verdict | Modern evidence | Behavior gap / notes |
|---|---|---|---|
| `EcoATM_PWS.PWS_OrderHistory` (page) + `DS_GetOrCreateOrderHistoryHelper` + `SUB_CalculateOrderHistoryTabTotals` — 4 tabs (All/Recent/In-Process/Complete) | IMPLEMENTED | `OrderHistoryController` `GET /pws/orders` + `/counts`; `OrderHistoryService`; `model/pws/OrderHistoryView`, `V37/V40 order_history_view`; `pws/orders/page.tsx` | Tab counts present. |
| `EcoATM_PWS.PWS_OrderDetails` (page) — by-SKU / by-device toggle | IMPLEMENTED | `GET /{offerId}/details/by-sku` + `/by-device`; `pws/orders/[id]`; `V38 order_detail_columns` | — |
| `EcoATM_PWS.Order_Overview` / `Offer_Overview` (page) — admin order/offer grids | IMPLEMENTED | `admin/pws-data-center/offers/page.tsx` | Consolidates Offers/OfferItems/Orders/OfferID/BuyerOffers per port doc. |
| `EcoATM_PWS.OrderStatus_Overview` / `OrderStatus_NewEdit` (page) — status reference CRUD | IMPLEMENTED | `settings/pws-control-center/order-status`; `PWSAdminController` `/admin/order-status` (list/create/update/delete) | — |
| `EcoATM_PWS.ChangeOrderStatus_Select` + `ACT_ChangeOfferStatus_Proceed` + `VAL_ChargeOfferStatusHelper_IsValid` (page/flow) — **bulk order/offer status migration** (date-range or selected orders, from/to safety guard) | MISSING | no `change-status`/bulk-status endpoint (searched) | Confirmed-absent. Legacy tool for correcting bad Oracle syncs in bulk. |
| `EcoATM_PWS.ManageFileDocument_SelectOrderStatusFile` (page) — import OrderStatus lookup from Oracle spreadsheet | MISSING | order-status CRUD is manual only | Minor; file-import path absent. |
| `EcoATM_PWS.PWS_TrackOrder` (page) — open external Oracle tracking link | PARTIAL | order detail present; no explicit external-tracking-link action located | Minor; may be embedded in order detail. |

### Pricing / future price
| Spec node | Verdict | Modern evidence | Behavior gap / notes |
|---|---|---|---|
| `EcoATM_PWS.PWS_Pricing` (page) — review device pricing + inventory, grid search | IMPLEMENTED | `PricingController` `GET /pws/pricing/devices` (paged); `pws/pricing/page.tsx` | — |
| `EcoATM_PWS.Page2_UploadData` (page) + `EcoATM_PWSMDM.JA_UpdateDevicePrices` — future-price Excel upload + scheduled price update + config | IMPLEMENTED | `PricingController` `POST /devices/upload`, `PUT /devices/{id}`, `/devices/bulk`; `FuturePriceConfigController` `GET/PUT /pws/pricing/config`; `V36 future_price_config` | — |
| `EcoATM_PWSMDM.PriceHistory_Overview` / `NewEdit` (page) | IMPLEMENTED (read) | `GET /pws/pricing/devices/{id}/history` | Read path present; dedicated edit form not separately verified (low value). |

### Admin / control-center / integration config
| Spec node | Verdict | Modern evidence | Behavior gap / notes |
|---|---|---|---|
| `EcoATM_PWSIntegration.DeposcoConfig_Overview` / `PWSConfiguration_Edit` (page) — Deposco config + test connection | IMPLEMENTED | `settings/pws-control-center/deposco`; `OracleConfigController /admin/oracle-config` (+`test-auth`); `V14 integration`, `V24 config` | Oracle test-auth present. Deposco-specific ping — see Partial below. |
| `EcoATM_PWSIntegration.ACT_TestDeposcoAPI` (flow) — Deposco credential/reachability ping | PARTIAL | Oracle `test-auth` exists; no Deposco `TestString` GET located | `ACT_TestDeposcoAPI` (GET `DeposcoConfig/TestString`) not reproduced as its own endpoint. |
| `EcoATM_PWS.PWSResponseConfig_Overview` (page) — error-code → friendly message map | IMPLEMENTED | `settings/pws-control-center/error-messages`; `PWSAdminController /admin/error-messages` (CRUD) | — |
| `EcoATM_PWS.PWSConstants_Overview` (page) — global config incl. reminder-hour thresholds, SLA days | IMPLEMENTED (storage) | `settings/pws-control-center/pws-constants`; `PWSAdminController` `/admin/pws-constants` (`sla_days`, `send_first/second_reminder`, `hours_first/second_counter_reminder`) | Config persists; **reminder sender job missing** (see Missing). |
| `EcoATM_PWS.MaintenanceMode_NewEdit` (page) | IMPLEMENTED | `settings/pws-control-center/maintenance-mode`; `PWSAdminController /admin/maintenance-mode` | — |
| `EcoATM_PWSIntegration.ManageFileDocument_ChooseFile` (page) — import error-mapping JSON | MISSING | error-messages CRUD manual only | Minor; file-import absent. |
| `EcoATM_PWS.PWSUserPersonalization_Overview` (page) — user personalization/idle-timeout | MISSING | not located | Minor; couldn't-locate. |
| `EcoATM_PWSIntegration.SUB_Oracle_Configuration` / `SUB_Oracle_ErrorMessage` (flow) — **inbound** Oracle push replacing PWSConfiguration / error table | MISSING | config edited via admin UI, no inbound XML-import endpoint | Likely-obsolete inbound integration (Oracle→Mendix XML push). |

### Deposco inventory sync (integration)
| Spec node | Verdict | Modern evidence | Behavior gap / notes |
|---|---|---|---|
| `EcoATM_PWS.SUB_LoadPWSInventory_Deposco` + `SUB_FetchItemsFromDeposco` + `SUB_LoadPWSInventory_Task_Deposco` — paged Deposco inventory pull, match Device by SKU, update AvailableQty, delta-warn, recalc reserved/facets | IMPLEMENTED | `AtpSyncService.fullInventorySync` → `fetchAllDeposcoInventory` (token + paged `/inventory` GET) → `applyAtpUpdates` (SKU match, availableQty/atpQty update, lastSyncTime) → `updateReservedQuantities`; `AtpSyncController` `POST /inventory/sync/full`, `/sync/simulate`, `GET /sync/logs`; `shipments/page.tsx` | Real HTTP paged fetch. Manual-trigger (legacy `ACT_FullInventorySync` was also button-triggered per port doc). |
| `EcoATM_PWSIntegration.ACT_GenerateDeposcoV2Token` / `SUB_GenerateDeposcoPassword` (flow) — Deposco OAuth token / Basic-auth header | IMPLEMENTED | `AtpSyncService.obtainDeposcoToken` (POST `/auth/token`) | Token caching (legacy cached AccessToken row) not reproduced — re-auths per sync; low risk. |
| `EcoATM_PWSIntegration.SUB_FetchDeposcoOrderNumber` (flow) — per-order Deposco order-number lookup (precursor to shipment history) | MISSING | shipments page uses sync logs; no Deposco order-search call | Confirmed-absent per-order Deposco order/shipment lookup. |

### Snowflake sync (integration)
| Spec node | Verdict | Modern evidence | Behavior gap / notes |
|---|---|---|---|
| `EcoATM_PWS.SUB_Offer_UpdateSnowflake` (flow) — serialize Offer → `JA_SnowflakeStoreProc` upsert after every accept/counter/decline/order | MISSING (stubbed) | `OfferService.java:671-672` `// SUB_Offer_UpdateSnowflake (stubbed) TODO: Sync offer data to Snowflake analytics` | Confirmed stub. Legacy is fire-and-log (non-error-propagating), so low functional risk, but analytics warehouse never receives PWS offer state. (`AggregatedInventorySnowflakeSyncService` exists but is auctions-inventory, a different capability.) |
| `EcoATM_PWS.ACT_Offers_UpdateOfferStatusSnowflake` / `Offer_UpdateSnowflake` (flow/page) — admin manual date-range Snowflake resync | MISSING | no resync endpoint/page | Follows from the stub above. |

### Emails
| Spec node | Verdict | Modern evidence | Behavior gap / notes |
|---|---|---|---|
| `SUB_SendPWSOfferConfirmationEmail` / `OrderConfirmationEmail` / `PendingOrderEmail` / `CounterOfferEmail` (flow) | IMPLEMENTED | `PWSEmailService` (4 methods) + `PwsOfferEmailListener` (`@TransactionalEventListener AFTER_COMMIT`) + `PwsOfferEmailEvent`; counter email fired from `OfferReviewService.completeReview:477` | 4 buyer emails wired via post-commit events. |
| `EcoATM_PWS.ACT_SendCounterOfferReminderEmails` / `SUB_SendCounterOfferReminderEmail` (flow) — first/second reminder cadence from `PWSConstants` thresholds, one-shot flags | MISSING | thresholds stored (`pws_constants.hours_first/second_counter_reminder`) but no scheduled sender; no `@Scheduled` PWS job (only Auth/Upload/Bid rate-limiters, AuctionLifecycle, ReserveBidSync, SessionCleanup) | Confirmed-absent automation — config exists but nothing sends the nag emails. |
| `SUB_SendPWSAdjustedQuantityOrderConfirmationEmail` (flow) — distinct adjusted-qty confirmation | DIVERGENT | mapped to `sendPendingOrderEmail` (per `PWSEmailService` javadoc) | Distinct adjusted-qty template collapsed into pending-order email. |

### Batch
| Spec node | Verdict | Modern evidence | Behavior gap / notes |
|---|---|---|---|
| `batch:SE_SetSLATag` — **every-minute** job tagging `Sales_Review`/`Buyer_Acceptance` offers past SLA cutoff | DIVERGENT | `PWSAdminController` `POST setSLATags` / `removeSLATags` (manual admin buttons, jdbc `UPDATE`); no `@Scheduled` | Behavior exists but as a manual lever, not an automatic 1-min cron — overdue offers are not auto-flagged. |

### RMA-adjacent (separate capability, noted for completeness)
| Spec node | Verdict | Modern evidence | Behavior gap / notes |
|---|---|---|---|
| RMA surface (RMA is its own capability; **no RMA pages in the PWS 63**) | (net-new here) | `RmaController /api/v1/pws/rma` (submit/summary/reasons/template/detail/approve-all/decline-all/item-status/complete-review); `pws/rma-requests`, `pws/rma-review`, `admin/pws-data-center/rma`, `settings/.../rma-status`+`rma-template`; `V33 rma_tables`, `V34 data_rma` | Full modern RMA surface exists under the PWS route tree — assess against the **RMA** capability partial, not counted in PWS totals. |

## Biggest gaps (named, with spec node ids)
1. **Snowflake offer-status sync stubbed** — `EcoATM_PWS.SUB_Offer_UpdateSnowflake` is a literal TODO in `OfferService`; the entire per-offer warehouse upsert and the admin manual resync (`ACT_Offers_UpdateOfferStatusSnowflake` / `Offer_UpdateSnowflake` page) are absent. PWS offer/order state never reaches Snowflake analytics.
2. **Counter-offer reminder automation missing** — `EcoATM_PWS.ACT_SendCounterOfferReminderEmails` / `SUB_SendCounterOfferReminderEmail`: thresholds are stored in `pws_constants` and editable in the UI, but there is no scheduled job that sends first/second reminder emails, so idle counter-offers are never nudged.
3. **`Resubmit to Oracle` recovery lever missing** — `EcoATM_PWS.ACT_Order_ReSubmitToOracle` (+ `Order_detail` action): no endpoint to re-send a failed/pending Oracle order. Ops cannot retry stuck orders from the modern app.
4. **Bulk change-order/offer-status tool missing** — `EcoATM_PWS.ChangeOrderStatus_Select` / `ACT_ChangeOfferStatus_Proceed` / `VAL_ChargeOfferStatusHelper_IsValid`: the date-range/selected-orders status-migration tool (with the from-status safety guard) used to correct bad Oracle syncs has no modern equivalent.
5. **SLA tagging is manual, not the 1-minute batch** — `batch:SE_SetSLATag`: modern exposes `setSLATags`/`removeSLATags` as admin buttons but ships no scheduler, so offers sitting too long in review/acceptance are not automatically flagged for sales.

Secondary: buyer **Excel offer upload** wizard (`BuyerOffer_Step1/Step2`), **Deposco per-order lookup** (`SUB_FetchDeposcoOrderNumber`), and the **device detail / build-offer drawer** (`PWS_DeviceView`) are absent/partial.

## Net-new modern behavior (not in legacy)
- **Full RMA module** under `/api/v1/pws/rma` + 4 frontend routes + `V33/V34` (its own capability; legacy PWS had no RMA pages in these 63).
- **Consolidated admin surface** — 19 legacy PWS-Data-Center datagrids collapsed to 5 screens (`docs/tasks/pws-data-center-port.md`); master-data 8 grids → 1 tabbed screen.
- **Audited soft-delete + reason** on device/master-data deletes (`V56 pws_admin_audit`) replacing legacy raw JSON-warning-log deletes; string filters default to `contains` not `=`.
- **Async sync with status drawer + `sync/logs`** endpoint and a `/sync/simulate` dev path for Deposco ATP (legacy exposed long-running batch as a bare button).
- **Feature-flag / error-message / ranks-config admin CRUD** surfaced as first-class settings pages.

## Likely-dead / obsolete legacy (don't port)
- `EcoATM_PWSIntegration.SUB_Oracle_Configuration` / `SUB_Oracle_ErrorMessage` — inbound Oracle→Mendix XML config/error-table push; modern edits config via admin UI, so the inbound replace-singleton endpoints are obsolete.
- `EcoATM_PWS.PropertiesUtility_Update` — bulk device-property merge/mass-edit power tool; not ported, likely superseded by audited master-data CRUD.
- `EcoATM_PWS.DS_GetEcoATMCounterOffers` / `DS_GetFinalOfferForCounterOffers` / `DS_GetOriginalOfferForCounterOffers` — the graph itself flags these as unfinished **stub** data sources (return a blank `OffersUiHelper`); do not port as-is — confirm intent with product.
- `*_Test` Deposco flows (`SUB_FetchItemsFromDeposco_Test`, `SUB_LoadPWSInventory_Task_Deposco_Test`) — sandbox validation harnesses; modern `/sync/simulate` covers the equivalent.
- `EcoATM_PWSMDM.Grade_Overview.ACT_CloneCaseLotDisplayNames` — one-off maintenance utility.

## Divergent behaviors (behavior differs, not just naming)
1. **Oracle toggle-off path** — legacy `SUB_Order_SendOrderToOracle`: API disabled → returns a *generic error* `OracleResponse` (routes to Pending_Order). Modern `OracleOrderClient.submitOrder`: config inactive/missing → returns a *simulated success* (`returnCode="00"`, `orderNumber="SIM-…"`) routing to **Ordered**. Different terminal offer status when the integration is off.
2. **Concurrent-edit lock** — legacy `EcoATM_Lock.JA_ExtractObjectInfo` soft-lock (another user holds it → bounce to read-only review page) replaced by `security/PwsOwnershipGuard` buyer-code **ownership** authorization. No concurrent-edit contention handling / read-only bounce.
3. **Adjusted-quantity confirmation email** — collapsed into the pending-order email (`PWSEmailService` javadoc), losing the distinct legacy template branch in `SUB_CreateOrderResponse_ManageResult`.
4. **Master-data delete audit** — legacy writes a JSON snapshot to the warning log; modern soft-deletes into a dedicated audit table with actor + reason (intentional, per port doc anti-pattern #2).
