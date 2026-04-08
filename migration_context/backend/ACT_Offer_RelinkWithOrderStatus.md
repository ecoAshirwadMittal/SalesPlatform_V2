# Microflow Detailed Specification: ACT_Offer_RelinkWithOrderStatus

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **DB Retrieve **EcoATM_PWS.Offer** Filter: `[OfferStatus!=empty] [not(EcoATM_PWS.Offer_OrderStatus/EcoATM_PWS.OrderStatus)]` (Result: **$OfferList**)**
3. 🔀 **DECISION:** `$OfferList!=empty`
   ➔ **If [true]:**
      1. **CreateList**
      2. **DB Retrieve **EcoATM_PWS.OrderStatus**  (Result: **$OrderStatusList**)**
      3. 🔄 **LOOP:** For each **$IteratorOffer** in **$OfferList**
         │ 1. **List Operation: **Find** on **$undefined** where `getCaption($IteratorOffer/OfferStatus)` (Result: **$OrderStatus**)**
         │ 2. 🔀 **DECISION:** `$OrderStatus!=empty`
         │    ➔ **If [true]:**
         │       1. **Update **$IteratorOffer**
      - Set **Offer_OrderStatus** = `$OrderStatus`**
         │       2. **Add **$$IteratorOffer
** to/from list **$OfferToCommitList****
         │    ➔ **If [false]:**
         └─ **End Loop**
      4. **Commit/Save **$OfferToCommitList** to Database**
      5. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      6. **Show Message (Information): `Data updated.`**
      7. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      2. **Show Message (Information): `All records already uptodate.`**
      3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.