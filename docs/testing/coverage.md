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
