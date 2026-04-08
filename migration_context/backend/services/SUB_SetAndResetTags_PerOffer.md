# Microflow Detailed Specification: SUB_SetAndResetTags_PerOffer

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **Call Microflow **EcoATM_PWS.SUB_SetSameSKUTag****
3. 🔀 **DECISION:** `($Offer/OfferStatus = EcoATM_PWS.ENUM_PWSOrderStatus.Ordered or $Offer/OfferStatus = EcoATM_PWS.ENUM_PWSOrderStatus.Declined)`
   ➔ **If [true]:**
      1. **Update **$Offer** (and Save to DB)
      - Set **SameSKUOffer** = `false`**
      2. **Retrieve related **OfferItem_Offer** via Association from **$Offer** (Result: **$OfferItemList**)**
      3. 🔄 **LOOP:** For each **$IteratorOfferItem** in **$OfferItemList**
         │ 1. **Update **$IteratorOfferItem**
      - Set **SameSKUOfferAvailable** = `false`**
         └─ **End Loop**
      4. **Commit/Save **$OfferItemList** to Database**
      5. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      6. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.