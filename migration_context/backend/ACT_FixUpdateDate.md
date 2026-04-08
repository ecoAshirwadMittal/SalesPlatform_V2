# Microflow Detailed Specification: ACT_FixUpdateDate

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_PWS.Order** Filter: `[OracleOrderStatus = 'Success']` (Result: **$OrderList**)**
2. **CreateList**
3. 🔄 **LOOP:** For each **$IteratorOrder** in **$OrderList**
   │ 1. **Retrieve related **Offer_Order** via Association from **$IteratorOrder** (Result: **$Offer**)**
   │ 2. 🔀 **DECISION:** `$Offer != empty`
   │    ➔ **If [true]:**
   │       1. **Update **$Offer**
      - Set **UpdateDate** = `$IteratorOrder/OrderDate`**
   │       2. **Add **$$Offer** to/from list **$OfferList****
   │    ➔ **If [false]:**
   └─ **End Loop**
4. **Commit/Save **$OfferList** to Database**
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.