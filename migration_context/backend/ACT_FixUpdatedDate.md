# Microflow Detailed Specification: ACT_FixUpdatedDate

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_PWS.Offer** Filter: `[UpdateDate = empty]` (Result: **$OfferList**)**
2. 🔄 **LOOP:** For each **$IteratorOffer** in **$OfferList**
   │ 1. **Update **$IteratorOffer**
      - Set **UpdateDate** = `$IteratorOffer/changedDate`**
   └─ **End Loop**
3. **Commit/Save **$OfferList** to Database**
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.