# Microflow Detailed Specification: SUB_RemoveSLATagsForAllOffers

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **DB Retrieve **EcoATM_PWS.Offer** Filter: `[(OfferStatus='Sales_Review') or (OfferStatus = 'Buyer_Acceptance' )]` (Result: **$OfferList**)**
3. 🔄 **LOOP:** For each **$IteratorOffer** in **$OfferList**
   │ 1. **Update **$IteratorOffer**
      - Set **OfferBeyondSLA** = `false`**
   └─ **End Loop**
4. **Commit/Save **$OfferList** to Database**
5. **Show Message (Information): `SLA tags have removed for all Offers!`**
6. **Call Microflow **Custom_Logging.SUB_Log_Info****
7. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.