# Microflow Detailed Specification: SUB_UpdateReservedQuanityPerCaseLot

### 📥 Inputs (Parameters)
- **$CaseLotList** (Type: EcoATM_PWSMDM.CaseLot)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **CreateList**
3. **DB Retrieve **EcoATM_PWS.OfferItem** Filter: `[Reserved] [EcoATM_PWS.OfferItem_Offer/EcoATM_PWS.Offer/OfferStatus='Ordered'] [EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/ItemType='SPB']` (Result: **$OfferItemList**)**
4. 🔄 **LOOP:** For each **$IteratorOfferItem** in **$OfferItemList**
   │ 1. **Retrieve related **OfferItem_CaseLot** via Association from **$IteratorOfferItem** (Result: **$CaseLot_OI**)**
   │ 2. **List Operation: **FindByExpression** on **$undefined** where `$currentObject=$CaseLot_OI` (Result: **$CaselotInList**)**
   │ 3. 🔀 **DECISION:** `$CaselotInList!=empty`
   │    ➔ **If [true]:**
   │       1. **Update **$IteratorOfferItem**
      - Set **Reserved** = `false`
      - Set **ReservedOn** = `empty`
      - Set **OrderSynced** = `true`**
   │       2. **Add **$$IteratorOfferItem
** to/from list **$OfferItemList_Filtered****
   │    ➔ **If [false]:**
   └─ **End Loop**
5. **Commit/Save **$OfferItemList** to Database**
6. 🔄 **LOOP:** For each **$Caselot** in **$CaseLotList**
   │ 1. **DB Retrieve **EcoATM_PWS.OfferItem** Filter: `[EcoATM_PWS.OfferItem_CaseLot=$Caselot] [Reserved]` (Result: **$OfferItemList_1**)**
   │ 2. **List Operation: **Subtract** on **$undefined** (Result: **$OfferItemList_ValidForCalculation**)**
   │ 3. **AggregateList**
   │ 4. **Update **$Caselot**
      - Set **CaseLotReservedQty** = `if($TotalReservedQuantity>$Caselot/CaseLotReservedQty) then $Caselot/CaseLotAvlQty else round($TotalReservedQuantity)`
      - Set **CaseLotATPQty** = `if($TotalReservedQuantity>$Caselot/CaseLotAvlQty) then 0 else $Caselot/CaseLotAvlQty- round($TotalReservedQuantity)`**
   └─ **End Loop**
7. **Commit/Save **$CaseLotList** to Database**
8. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
9. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.