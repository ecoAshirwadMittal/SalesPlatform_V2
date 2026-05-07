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
Target 85%+. R2 cascade (Only_Qualified branches × 6 + qual_mode shortcuts × 3),
R3 cascade (4 branches + all-NULL fallthrough + DISTINCT-ON R2-over-R1 + rank-NULL
guard = 7 cases), STB shortcut for R2 + R3 (× 2), R1 regression sanity (× 1).
See `BidDataCreationRepositoryIT` (20 new cases added by sub-project 5b) and
`BidDataScenario` builder extensions (7 new fluent primitives).
