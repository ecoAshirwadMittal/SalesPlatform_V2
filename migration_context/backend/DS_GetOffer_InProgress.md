# Microflow Detailed Specification: DS_GetOffer_InProgress

### 📥 Inputs (Parameters)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'PWSOrderOfferInProgress'`**
2. **Create Variable **$Description** = `'Get Offer In Progress.'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **Create Variable **$InProgress_OrderStatus** = `empty`**
5. **DB Retrieve **EcoATM_PWS.BuyerOffer** Filter: `[EcoATM_PWS.BuyerOffer_BuyerCode = $BuyerCode] [OfferStatus = $InProgress_OrderStatus]` (Result: **$LastBuyerOffer**)**
6. 🔀 **DECISION:** `$LastBuyerOffer!= empty`
   ➔ **If [true]:**
      1. **Retrieve related **BuyerOfferItem_BuyerOffer** via Association from **$LastBuyerOffer** (Result: **$BuyerOfferItemList**)**
      2. **Call Microflow **EcoATM_PWS.SUB_CalculateTotals_OnChange****
      3. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/TotalPrice != empty and $currentObject/TotalPrice > 0` (Result: **$BuyerOfferItemList_Valid**)**
      4. 🔄 **LOOP:** For each **$IteratorBuyerOfferItem** in **$BuyerOfferItemList_Valid**
         │ 1. 🔀 **DECISION:** `$IteratorBuyerOfferItem/_Type = EcoATM_PWS.ENUM_BuyerOfferItemType.Functional_Case_Lot`
         │    ➔ **If [true]:**
         │       1. **Retrieve related **BuyerOfferItem_CaseLot** via Association from **$IteratorBuyerOfferItem** (Result: **$CaseLot**)**
         │       2. **Update **$IteratorBuyerOfferItem**
      - Set **IsExceedQty** = `if $CaseLot != empty and $CaseLot/CaseLotATPQty != empty and $IteratorBuyerOfferItem/Quantity != empty then $IteratorBuyerOfferItem/Quantity > $CaseLot/CaseLotATPQty and $CaseLot/CaseLotATPQty<= 100 else false`
      - Set **CSSClass** = `''`**
         │    ➔ **If [false]:**
         │       1. **Update **$IteratorBuyerOfferItem**
      - Set **IsExceedQty** = `if $IteratorBuyerOfferItem/Quantity != empty and $IteratorBuyerOfferItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/ATPQty != empty then ($IteratorBuyerOfferItem/Quantity > $IteratorBuyerOfferItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/ATPQty and $IteratorBuyerOfferItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/ATPQty <= 100) else false`
      - Set **CSSClass** = `''`**
         └─ **End Loop**
      5. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/Quantity=empty and $currentObject/TotalPrice=0` (Result: **$BuyerOfferItemList_TotalZero**)**
      6. 🔄 **LOOP:** For each **$IteratorBuyerOfferItem_2** in **$BuyerOfferItemList_TotalZero**
         │ 1. 🔀 **DECISION:** `$IteratorBuyerOfferItem_2/_Type = EcoATM_PWS.ENUM_BuyerOfferItemType.Functional_Case_Lot`
         │    ➔ **If [true]:**
         │       1. **Update **$IteratorBuyerOfferItem_2**
      - Set **TotalPrice** = `empty`
      - Set **CaseOfferTotalPrice** = `empty`**
         │    ➔ **If [false]:**
         │       1. **Update **$IteratorBuyerOfferItem_2**
      - Set **TotalPrice** = `empty`**
         └─ **End Loop**
      7. **List Operation: **FilterByExpression** on **$undefined** where `($currentObject/Quantity = empty or $currentObject/Quantity > 0) and ( if $currentObject/_Type = EcoATM_PWS.ENUM_BuyerOfferItemType.Functional_Case_Lot then $currentObject/CaseOfferTotalPrice != empty else $currentObject/TotalPrice != empty )` (Result: **$BuyerOfferItemList_Modified**)**
      8. 🔄 **LOOP:** For each **$IteratorBuyerOfferItem_Modified** in **$BuyerOfferItemList_Modified**
         │ 1. **Update **$IteratorBuyerOfferItem_Modified**
      - Set **IsModified** = `true`**
         └─ **End Loop**
      9. **Commit/Save **$BuyerOfferItemList_TotalZero** to Database**
      10. **List Operation: **Find** on **$undefined** where `true` (Result: **$BuyerOfferItem_ExceedingQty**)**
      11. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/ItemType = 'SPB' and $currentObject/IsModified` (Result: **$BuyerOfferItem_ModifiedCaseLot**)**
      12. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/ItemType = 'PWS' and $currentObject/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Grade/EcoATM_PWSMDM.Grade/Grade != 'A_YYY' and $currentObject/IsModified` (Result: **$BuyerOfferItem_ModifiedFunctionalDevice**)**
      13. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/ItemType = 'PWS' and $currentObject/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Grade/EcoATM_PWSMDM.Grade/Grade = 'A_YYY' and $currentObject/IsModified` (Result: **$BuyerOfferItem_ModifiedUntestedDevice**)**
      14. **Update **$LastBuyerOffer** (and Save to DB)
      - Set **IsExceedingQty** = `$BuyerOfferItem_ExceedingQty != empty`
      - Set **IsFunctionalDevicesExist** = `$BuyerOfferItem_ModifiedFunctionalDevice != empty`
      - Set **IsCaseLotsExist** = `$BuyerOfferItem_ModifiedCaseLot != empty`
      - Set **IsUntestedDeviceExist** = `$BuyerOfferItem_ModifiedUntestedDevice != empty`**
      15. **Commit/Save **$BuyerOfferItemList_Valid** to Database**
      16. **Commit/Save **$BuyerOfferItemList_Modified** to Database**
      17. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      18. 🏁 **END:** Return `$LastBuyerOffer`
   ➔ **If [false]:**
      1. **Create **EcoATM_PWS.BuyerOffer** (Result: **$NewOrder**)
      - Set **OfferStatus** = `empty`
      - Set **OfferTotal** = `0`
      - Set **BuyerOffer_BuyerCode** = `$BuyerCode`**
      2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      3. 🏁 **END:** Return `$NewOrder`

**Final Result:** This process concludes by returning a [Object] value.