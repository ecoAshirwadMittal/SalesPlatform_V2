# Microflow Detailed Specification: SUB_ReserveQuantityForDevice

### 📥 Inputs (Parameters)
- **$OfferItem** (Type: EcoATM_PWS.OfferItem)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **OfferItem_Device** via Association from **$OfferItem** (Result: **$Device**)**
2. **Create Variable **$ReservationNeeded** = `if($OfferItem/EcoATM_PWS.OfferItem_Offer/EcoATM_PWS.Offer/OfferStatus=EcoATM_PWS.ENUM_PWSOrderStatus.Sales_Review and ($OfferItem/SalesOfferItemStatus= EcoATM_PWS.ENUM_OfferItemStatus.Accept or $OfferItem/SalesOfferItemStatus= EcoATM_PWS.ENUM_OfferItemStatus.Finalize )) then true else if (($OfferItem/EcoATM_PWS.OfferItem_Offer/EcoATM_PWS.Offer/OfferStatus=EcoATM_PWS.ENUM_PWSOrderStatus.Ordered or $OfferItem/EcoATM_PWS.OfferItem_Offer/EcoATM_PWS.Offer/OfferStatus=EcoATM_PWS.ENUM_PWSOrderStatus.Pending_Order) and ($OfferItem/SalesOfferItemStatus= EcoATM_PWS.ENUM_OfferItemStatus.Accept or $OfferItem/SalesOfferItemStatus= EcoATM_PWS.ENUM_OfferItemStatus.Finalize or ($OfferItem/SalesOfferItemStatus= EcoATM_PWS.ENUM_OfferItemStatus.Counter and $OfferItem/BuyerCounterStatus= EcoATM_PWS.ENUM_CounterStatus.Accept))) then true else if ($OfferItem/EcoATM_PWS.OfferItem_Offer/EcoATM_PWS.Offer/OfferStatus=EcoATM_PWS.ENUM_PWSOrderStatus.Buyer_Acceptance and $OfferItem/SalesOfferItemStatus= EcoATM_PWS.ENUM_OfferItemStatus.Accept ) then true else false`**
3. **Call Microflow **EcoATM_PWS.SUB_GenerateReservedQuantityForDevice** (Result: **$TotalReservedQuantity**)**
4. **Update **$Device**
      - Set **ReservedQty** = `if($TotalReservedQuantity>$Device/AvailableQty) then $Device/AvailableQty else round($TotalReservedQuantity)`
      - Set **ATPQty** = `if($TotalReservedQuantity>$Device/AvailableQty) then 0 else $Device/AvailableQty- round($TotalReservedQuantity)`**
5. **Update **$OfferItem**
      - Set **Reserved** = `$ReservationNeeded`
      - Set **ReservedOn** = `if($ReservationNeeded) then [%CurrentDateTime%] else empty`**
6. 🏁 **END:** Return `$Device`

**Final Result:** This process concludes by returning a [Object] value.