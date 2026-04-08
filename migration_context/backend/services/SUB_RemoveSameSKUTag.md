# Microflow Detailed Specification: SUB_RemoveSameSKUTag

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_PWS.Offer** Filter: `[OfferStatus = 'Ordered' or OfferStatus = 'Declined'] [SameSKUOffer]` (Result: **$OfferList_OrderedOrDeclinedWithSKU**)**
2. 🔄 **LOOP:** For each **$IteratorOffer** in **$OfferList_OrderedOrDeclinedWithSKU**
   │ 1. **Update **$IteratorOffer**
      - Set **SameSKUOffer** = `false`**
   └─ **End Loop**
3. **DB Retrieve **EcoATM_PWS.OfferItem** Filter: `[SameSKUOfferAvailable] [EcoATM_PWS.OfferItem_Offer/EcoATM_PWS.Offer/OfferStatus = 'Ordered' or EcoATM_PWS.OfferItem_Offer/EcoATM_PWS.Offer/OfferStatus = 'Declined']` (Result: **$OfferItemList**)**
4. 🔄 **LOOP:** For each **$IteratorOfferItem** in **$OfferItemList**
   │ 1. **Update **$IteratorOfferItem**
      - Set **SameSKUOfferAvailable** = `false`**
   └─ **End Loop**
5. **Commit/Save **$OfferList_OrderedOrDeclinedWithSKU** to Database**
6. **Commit/Save **$OfferItemList** to Database**
7. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.