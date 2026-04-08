# Microflow Detailed Specification: SUB_SetAndResetTags

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **DB Retrieve **EcoATM_PWS.Offer** Filter: `[OfferStatus = 'Buyer_Acceptance' or OfferStatus = 'Sales_Review']` (Result: **$OfferList_SaleReviewOrBuyerAcceptance**)**
3. 🔄 **LOOP:** For each **$IteratorOffer** in **$OfferList_SaleReviewOrBuyerAcceptance**
   │ 1. **Call Microflow **EcoATM_PWS.SUB_SetSameSKUTag****
   └─ **End Loop**
4. **Call Microflow **EcoATM_PWS.SUB_RemoveSameSKUTag****
5. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
6. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.