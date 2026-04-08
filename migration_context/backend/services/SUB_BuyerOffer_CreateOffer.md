# Microflow Detailed Specification: SUB_BuyerOffer_CreateOffer

### 📥 Inputs (Parameters)
- **$BuyerOffer** (Type: EcoATM_PWS.BuyerOffer)
- **$BuyerOfferItemList** (Type: EcoATM_PWS.BuyerOfferItem)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$ENUM_OfferType** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'CreateOffer'`**
2. **Create Variable **$Description** = `'Create Offer based on the BuyerOffer [BuyerOfferOfferID:'+$BuyerOffer/OfferID+'] [BuyerCode:'+$BuyerCode/Code+']'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **DB Retrieve **Administration.Account** Filter: `[id = $currentUser]` (Result: **$Account**)**
5. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Grade/EcoATM_PWSMDM.Grade/Grade != 'A_YYY' and $currentObject/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/ItemType = 'PWS'` (Result: **$BuyerOfferItem_FunctionalDevice**)**
6. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/EcoATM_PWSMDM.Device_Grade/EcoATM_PWSMDM.Grade/Grade = 'A_YYY' and $currentObject/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/ItemType = 'PWS'` (Result: **$BuyerOfferItem_UntestedDevice**)**
7. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/ItemType = 'SPB'` (Result: **$BuyerOfferItem_CaseLot**)**
8. **Create **EcoATM_PWS.Offer** (Result: **$NewOffer**)
      - Set **OfferSKUCount** = `$BuyerOffer/OfferSKUs`
      - Set **OfferTotalQuantity** = `$BuyerOffer/OfferQuantity`
      - Set **OfferTotalPrice** = `$BuyerOffer/OfferTotal`
      - Set **OfferSubmissionDate** = `[%CurrentDateTime%]`
      - Set **Offer_BuyerCode** = `$BuyerOffer/EcoATM_PWS.BuyerOffer_BuyerCode`
      - Set **OfferSubmittedBy_Account** = `$Account`
      - Set **Offer_SalesRepresentative** = `$BuyerOffer/EcoATM_PWS.BuyerOffer_BuyerCode/EcoATM_BuyerManagement.BuyerCode/EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/EcoATM_BuyerManagement.Buyer_SalesRepresentative`
      - Set **IsFunctionalDeviceExist** = `if $BuyerOfferItem_FunctionalDevice != empty then true else false`
      - Set **IsUntestedDeviceExist** = `if $BuyerOfferItem_UntestedDevice != empty then true else false`
      - Set **IsCaseLotsExist** = `if $BuyerOfferItem_CaseLot != empty then true else false`**
9. **Call Microflow **EcoATM_PWS.ACr_UpdateOfferID****
10. **CreateList**
11. 🔄 **LOOP:** For each **$IteratorBuyerOfferItem** in **$BuyerOfferItemList**
   │ 1. **Call Microflow **EcoATM_PWS.SUB_CalculatePurcentage** (Result: **$MinPurcentage**)**
   │ 2. **Call Microflow **EcoATM_PWS.SUB_CalculatePurcentage** (Result: **$ListPurcentage**)**
   │ 3. 🔀 **DECISION:** `$IteratorBuyerOfferItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/ItemType='SPB'`
   │    ➔ **If [false]:**
   │       1. **Create **EcoATM_PWS.OfferItem** (Result: **$NewOfferItem**)
      - Set **OfferQuantity** = `$IteratorBuyerOfferItem/Quantity`
      - Set **OfferPrice** = `$IteratorBuyerOfferItem/OfferPrice`
      - Set **OfferTotalPrice** = `$IteratorBuyerOfferItem/TotalPrice`
      - Set **SalesOfferItemStatus** = `if($ENUM_OfferType= EcoATM_PWS.ENUM_OfferType.FinalizeOrderFlow) then EcoATM_PWS.ENUM_OfferItemStatus.Finalize else if $IteratorBuyerOfferItem/OfferPrice>= $IteratorBuyerOfferItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/CurrentListPrice then EcoATM_PWS.ENUM_OfferItemStatus.Accept else if $IteratorBuyerOfferItem/OfferPrice< $IteratorBuyerOfferItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/CurrentListPrice then EcoATM_PWS.ENUM_OfferItemStatus.Counter else empty`
      - Set **MinPercentage** = `$MinPurcentage`
      - Set **ListPercentage** = `$ListPurcentage`
      - Set **FinalOfferPrice** = `$IteratorBuyerOfferItem/OfferPrice`
      - Set **FinalOfferQuantity** = `if $ENUM_OfferType = EcoATM_PWS.ENUM_OfferType.BuyNowFlow and $IteratorBuyerOfferItem/Quantity > $IteratorBuyerOfferItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/AvailableQty then $IteratorBuyerOfferItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/AvailableQty else $IteratorBuyerOfferItem/Quantity`
      - Set **OfferItem_Offer** = `$NewOffer`
      - Set **OfferItem_Device** = `$IteratorBuyerOfferItem/EcoATM_PWS.BuyerOfferItem_Device`
      - Set **OfferItem_BuyerCode** = `$IteratorBuyerOfferItem/EcoATM_PWS.BuyerOfferItem_BuyerCode`
      - Set **CounterQuantity** = `$IteratorBuyerOfferItem/Quantity`
      - Set **OfferItem_CaseLot** = `if $IteratorBuyerOfferItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/ItemType = 'SPB' then $IteratorBuyerOfferItem/EcoATM_PWS.BuyerOfferItem_CaseLot/EcoATM_PWSMDM.CaseLot else empty`**
   │       2. **Update **$NewOfferItem**
      - Set **FinalOfferTotalPrice** = `$NewOfferItem/FinalOfferQuantity*$NewOfferItem/FinalOfferPrice`**
   │       3. **Add **$$NewOfferItem
** to/from list **$OfferItemList****
   │    ➔ **If [true]:**
   │       1. **Retrieve related **BuyerOfferItem_CaseLot** via Association from **$IteratorBuyerOfferItem** (Result: **$CaseLot**)**
   │       2. **Create **EcoATM_PWS.OfferItem** (Result: **$NewOfferItem_Caselot**)
      - Set **OfferQuantity** = `$IteratorBuyerOfferItem/Quantity*$CaseLot/CaseLotSize`
      - Set **OfferPrice** = `$IteratorBuyerOfferItem/OfferPrice`
      - Set **OfferTotalPrice** = `$IteratorBuyerOfferItem/CaseOfferTotalPrice`
      - Set **SalesOfferItemStatus** = `if($ENUM_OfferType= EcoATM_PWS.ENUM_OfferType.FinalizeOrderFlow) then EcoATM_PWS.ENUM_OfferItemStatus.Finalize else if $IteratorBuyerOfferItem/OfferPrice>= $IteratorBuyerOfferItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/CurrentListPrice then EcoATM_PWS.ENUM_OfferItemStatus.Accept else if $IteratorBuyerOfferItem/OfferPrice< $IteratorBuyerOfferItem/EcoATM_PWS.BuyerOfferItem_Device/EcoATM_PWSMDM.Device/CurrentListPrice then EcoATM_PWS.ENUM_OfferItemStatus.Counter else empty`
      - Set **MinPercentage** = `$MinPurcentage`
      - Set **ListPercentage** = `$ListPurcentage`
      - Set **FinalOfferPrice** = `$IteratorBuyerOfferItem/OfferPrice`
      - Set **FinalOfferQuantity** = `if $ENUM_OfferType = EcoATM_PWS.ENUM_OfferType.BuyNowFlow and $IteratorBuyerOfferItem/Quantity > $IteratorBuyerOfferItem/EcoATM_PWS.BuyerOfferItem_CaseLot/EcoATM_PWSMDM.CaseLot/CaseLotATPQty then $CaseLot/CaseLotATPQty*$CaseLot/CaseLotSize else $IteratorBuyerOfferItem/Quantity*$CaseLot/CaseLotSize`
      - Set **OfferItem_Offer** = `$NewOffer`
      - Set **OfferItem_Device** = `$IteratorBuyerOfferItem/EcoATM_PWS.BuyerOfferItem_Device`
      - Set **OfferItem_BuyerCode** = `$IteratorBuyerOfferItem/EcoATM_PWS.BuyerOfferItem_BuyerCode`
      - Set **CounterQuantity** = `$IteratorBuyerOfferItem/Quantity`
      - Set **OfferItem_CaseLot** = `$CaseLot`**
   │       3. **Update **$NewOfferItem_Caselot**
      - Set **FinalOfferTotalPrice** = `$NewOfferItem_Caselot/FinalOfferQuantity*$NewOfferItem_Caselot/FinalOfferPrice`**
   │       4. **Add **$$NewOfferItem_Caselot
** to/from list **$OfferItemList****
   └─ **End Loop**
12. **AggregateList**
13. **AggregateList**
14. **Call Microflow **EcoATM_PWS.SUB_RetrieveOrderStatus** (Result: **$OrderStatus**)**
15. **Update **$NewOffer**
      - Set **OfferTotalQuantity** = `$SumTotalQuantity`
      - Set **OfferTotalPrice** = `$SumTotalPrice`
      - Set **OfferStatus** = `EcoATM_PWS.ENUM_PWSOrderStatus.Sales_Review`
      - Set **FinalOfferTotalSKU** = `$BuyerOffer/OfferSKUs`
      - Set **FinalOfferTotalQty** = `$SumTotalQuantity`
      - Set **FinalOfferTotalPrice** = `$SumTotalPrice`
      - Set **UpdateDate** = `[%CurrentDateTime%]`
      - Set **Offer_OrderStatus** = `$OrderStatus`**
16. **Call Microflow **EcoATM_PWS.SUB_CreateOriginalOfferSummary** (Result: **$OriginalOfferSummary**)**
17. **Commit/Save **$OfferItemList** to Database**
18. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
19. 🏁 **END:** Return `$NewOffer`

**Final Result:** This process concludes by returning a [Object] value.