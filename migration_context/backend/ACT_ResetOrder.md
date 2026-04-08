# Microflow Detailed Specification: ACT_ResetOrder

### 📥 Inputs (Parameters)
- **$BuyerOffer** (Type: EcoATM_PWS.BuyerOffer)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'PWSResetOrder'`**
2. **Create Variable **$Description** = `'Reset Order BuyerOffer [OfferId:'+$BuyerOffer/OfferID+'] [BuyerCode:'+$BuyerCode/Code+']'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **Update **$BuyerOffer** (and Save to DB)
      - Set **OfferTotal** = `0`
      - Set **OfferSKUs** = `0`
      - Set **OfferQuantity** = `0`**
5. **Retrieve related **BuyerOfferItem_BuyerOffer** via Association from **$BuyerOffer** (Result: **$OfferItemList**)**
6. 🔄 **LOOP:** For each **$IteratorOfferItem** in **$OfferItemList**
   │ 1. **Retrieve related **BuyerOfferItem_Device** via Association from **$IteratorOfferItem** (Result: **$Device**)**
   │ 2. **Retrieve related **Device_Grade** via Association from **$Device** (Result: **$Grade**)**
   │ 3. **Update **$IteratorOfferItem**
      - Set **Quantity** = `empty`
      - Set **OfferPrice** = `$Device/CurrentListPrice`
      - Set **TotalPrice** = `empty`
      - Set **IsExceedQty** = `false`
      - Set **IsModified** = `false`
      - Set **CaseOfferTotalPrice** = `empty`
      - Set **IsLowerthenCurrentPrice** = `false`
      - Set **_Type** = `if $Device/ItemType = 'SPB' then EcoATM_PWS.ENUM_BuyerOfferItemType.Functional_Case_Lot else if $Device/ItemType = 'PWS' and $Grade/Grade != empty and $Grade/Grade = 'A_YYY' then EcoATM_PWS.ENUM_BuyerOfferItemType.Untested_Device else EcoATM_PWS.ENUM_BuyerOfferItemType.Functional_Device`**
   │ 4. **Call Microflow **EcoATM_PWS.CAL_BuyerOfferItem_CSSStyle** (Result: **$CSSClass**)**
   │ 5. **Update **$IteratorOfferItem**
      - Set **CSSClass** = `''`**
   └─ **End Loop**
7. **Commit/Save **$OfferItemList** to Database**
8. **Update **$BuyerCode****
9. **Close current page/popup**
10. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
11. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.