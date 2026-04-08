# Microflow Detailed Specification: SUB_SetSLATag_Admin

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **Call Microflow **EcoATM_PWS.SUB_CalculateSLADate** (Result: **$ResultDate**)**
3. **DB Retrieve **EcoATM_PWS.Offer** Filter: `[(OfferStatus='Sales_Review') or (OfferStatus = 'Buyer_Acceptance' )]` (Result: **$OfferList**)**
4. **List Operation: **FilterByExpression** on **$undefined** where `trimToDays($currentObject/UpdateDate) <= trimToDays($ResultDate)` (Result: **$OfferList_filtered**)**
5. 🔄 **LOOP:** For each **$IteratorOffer** in **$OfferList_filtered**
   │ 1. **Update **$IteratorOffer**
      - Set **OfferBeyondSLA** = `true`**
   └─ **End Loop**
6. **Commit/Save **$OfferList_filtered** to Database**
7. **Show Message (Information): `SLA tags have been set!`**
8. **Call Microflow **Custom_Logging.SUB_Log_Info****
9. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.