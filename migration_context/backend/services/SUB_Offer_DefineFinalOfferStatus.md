# Microflow Detailed Specification: SUB_Offer_DefineFinalOfferStatus

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$Description** = `'Define final Offer status for offer [OfferId:'+$Offer/OfferID+']'`**
2. **Call Microflow **Custom_Logging.SUB_Log_Info****
3. **Retrieve related **OfferItem_Offer** via Association from **$Offer** (Result: **$OfferItemList_1**)**
4. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/SalesOfferItemStatus=EcoATM_PWS.ENUM_OfferItemStatus.Accept or $currentObject/BuyerCounterStatus=EcoATM_PWS.ENUM_CounterStatus.Accept` (Result: **$ExistOfferItem**)**
5. 🏁 **END:** Return `if($ExistOfferItem!=empty) then EcoATM_PWS.ENUM_PWSOrderStatus.Ordered else EcoATM_PWS.ENUM_PWSOrderStatus.Declined`

**Final Result:** This process concludes by returning a [Enumeration] value.