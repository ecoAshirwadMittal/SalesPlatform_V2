# Microflow Detailed Specification: SUB_CalculateTotals_OnChange

### 📥 Inputs (Parameters)
- **$BuyerOfferItemList** (Type: EcoATM_PWS.BuyerOfferItem)
- **$LastBuyerOffer** (Type: EcoATM_PWS.BuyerOffer)

### ⚙️ Execution Flow (Logic Steps)
1. **List Operation: **Filter** on **$undefined** where `empty` (Result: **$BuyerOfferItemList_EmptyTypes**)**
2. 🔄 **LOOP:** For each **$IteratorBuyerOfferItem_Empty** in **$BuyerOfferItemList_EmptyTypes**
   │ 1. **Retrieve related **BuyerOfferItem_Device** via Association from **$IteratorBuyerOfferItem_Empty** (Result: **$Device**)**
   │ 2. **Retrieve related **Device_Grade** via Association from **$Device** (Result: **$Grade**)**
   │ 3. **Update **$IteratorBuyerOfferItem_Empty**
      - Set **_Type** = `if $Device/ItemType = 'SPB' then EcoATM_PWS.ENUM_BuyerOfferItemType.Functional_Case_Lot else if $Device/ItemType = 'PWS' and $Grade/Grade != empty and $Grade/Grade = 'A_YYY' then EcoATM_PWS.ENUM_BuyerOfferItemType.Untested_Device else EcoATM_PWS.ENUM_BuyerOfferItemType.Functional_Device`**
   └─ **End Loop**
3. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/TotalPrice != empty and $currentObject/TotalPrice > 0` (Result: **$BuyerOfferItemList_Valid**)**
4. **List Operation: **Filter** on **$undefined** where `true` (Result: **$BuyerOfferItem_RedList_Qty**)**
5. **List Operation: **Filter** on **$undefined** where `EcoATM_PWS.ENUM_BuyerOfferItemType.Functional_Device` (Result: **$BuyerOfferItemList_FunctionalDevices**)**
6. **AggregateList**
7. **List Operation: **Filter** on **$undefined** where `EcoATM_PWS.ENUM_BuyerOfferItemType.Functional_Case_Lot` (Result: **$BuyerOfferItemList_SPB**)**
8. **Call Microflow **EcoATM_PWS.SUB_BuyerOfferItem_CalculateCaseLotSKUs** (Result: **$TotalSKUs_CaseLots**)**
9. **List Operation: **Filter** on **$undefined** where `EcoATM_PWS.ENUM_BuyerOfferItemType.Untested_Device` (Result: **$BuyerOfferItemList_UntestedDevices**)**
10. **AggregateList**
11. **AggregateList**
12. **AggregateList**
13. **AggregateList**
14. **AggregateList**
15. **AggregateList**
16. **AggregateList**
17. **Update **$LastBuyerOffer** (and Save to DB)
      - Set **OfferTotal** = `$SumTotalPrice_FunctionalDevices + $SumTotalPrice_SPB + $SumTotalPrice_UntestedDevices`
      - Set **OfferSKUs** = `round($TotalSKUs_FunctionalDevices + $TotalSKUs_CaseLots + $TotalSKUs_UntestedDevices)`
      - Set **OfferQuantity** = `round($TotalQuantity_FunctionalDevices + $TotalQuantity_SPB + $TotalQuantity_UntestedDevices)`
      - Set **IsExceedingQty** = `length($BuyerOfferItem_RedList_Qty) > 0`**
18. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.