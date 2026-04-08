# Microflow Detailed Specification: ACT_Offer_UpdateDrawerStatus

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_PWS.Offer**  (Result: **$OfferList**)**
2. 🔄 **LOOP:** For each **$IteratorOffer** in **$OfferList**
   │ 1. **Call Microflow **EcoATM_PWS.SUB_UpdateOfferDrawerStatus****
   └─ **End Loop**
3. **Show Message (Information): `Status Update Completed`**
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.