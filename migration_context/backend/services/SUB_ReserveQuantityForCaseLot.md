# Microflow Detailed Specification: SUB_ReserveQuantityForCaseLot

### 📥 Inputs (Parameters)
- **$OfferItem** (Type: EcoATM_PWS.OfferItem)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **OfferItem_CaseLot** via Association from **$OfferItem** (Result: **$CaseLot**)**
2. **Create Variable **$ReservationNeeded_CaseLot** = `if($OfferItem/EcoATM_PWS.OfferItem_Offer/EcoATM_PWS.Offer/OfferStatus=EcoATM_PWS.ENUM_PWSOrderStatus.Sales_Review and ($OfferItem/SalesOfferItemStatus= EcoATM_PWS.ENUM_OfferItemStatus.Accept or $OfferItem/SalesOfferItemStatus= EcoATM_PWS.ENUM_OfferItemStatus.Finalize )) then true else if (($OfferItem/EcoATM_PWS.OfferItem_Offer/EcoATM_PWS.Offer/OfferStatus=EcoATM_PWS.ENUM_PWSOrderStatus.Ordered or $OfferItem/EcoATM_PWS.OfferItem_Offer/EcoATM_PWS.Offer/OfferStatus=EcoATM_PWS.ENUM_PWSOrderStatus.Pending_Order) and ($OfferItem/SalesOfferItemStatus= EcoATM_PWS.ENUM_OfferItemStatus.Accept or $OfferItem/SalesOfferItemStatus= EcoATM_PWS.ENUM_OfferItemStatus.Finalize or ($OfferItem/SalesOfferItemStatus= EcoATM_PWS.ENUM_OfferItemStatus.Counter and $OfferItem/BuyerCounterStatus= EcoATM_PWS.ENUM_CounterStatus.Accept))) then true else if ($OfferItem/EcoATM_PWS.OfferItem_Offer/EcoATM_PWS.Offer/OfferStatus=EcoATM_PWS.ENUM_PWSOrderStatus.Buyer_Acceptance and $OfferItem/SalesOfferItemStatus= EcoATM_PWS.ENUM_OfferItemStatus.Accept ) then true else false`**
3. **Call Microflow **EcoATM_PWS.SUB_GenerateReservedQuantityForCaseLot** (Result: **$TotalReservedQuantity_CaseLot**)**
4. **Update **$CaseLot**
      - Set **CaseLotReservedQty** = `if($TotalReservedQuantity_CaseLot>$CaseLot/CaseLotAvlQty) then $CaseLot/CaseLotAvlQty else round($TotalReservedQuantity_CaseLot)`
      - Set **CaseLotATPQty** = `if($TotalReservedQuantity_CaseLot>$CaseLot/CaseLotAvlQty) then 0 else $CaseLot/CaseLotAvlQty- round($TotalReservedQuantity_CaseLot)`**
5. **Update **$OfferItem**
      - Set **Reserved** = `$ReservationNeeded_CaseLot`
      - Set **ReservedOn** = `if($ReservationNeeded_CaseLot) then [%CurrentDateTime%] else empty`**
6. 🏁 **END:** Return `$CaseLot`

**Final Result:** This process concludes by returning a [Object] value.