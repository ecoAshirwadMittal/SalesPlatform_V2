# Microflow Detailed Specification: SUB_SetSameSKUTag

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **Offer_BuyerCode** via Association from **$Offer** (Result: **$BuyerCode**)**
2. **Call Microflow **Custom_Logging.SUB_Log_Debug****
3. **CreateList**
4. **Retrieve related **OfferItem_Offer** via Association from **$Offer** (Result: **$OfferItemList_Current**)**
5. **CreateList**
6. 🔄 **LOOP:** For each **$IteratorOfferItem_Current** in **$OfferItemList_Current**
   │ 1. **Create Variable **$SKU** = `$IteratorOfferItem_Current/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/SKU`**
   │ 2. **DB Retrieve **EcoATM_PWS.OfferItem** Filter: `[EcoATM_PWS.OfferItem_Offer/EcoATM_PWS.Offer/OfferStatus = 'Sales_Review' or EcoATM_PWS.OfferItem_Offer/EcoATM_PWS.Offer/OfferStatus = 'Buyer_Acceptance'] [EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/SKU = $SKU] [SalesOfferItemStatus = 'Accept' or SalesOfferItemStatus = 'Counter' or SalesOfferItemStatus ='Finalize']` (Result: **$OfferItemList**)**
   │ 3. **Add **$$OfferItemList
** to/from list **$OfferItemList_Complete****
   └─ **End Loop**
7. 🔄 **LOOP:** For each **$IteratorOfferItem** in **$OfferItemList_Complete**
   │ 1. **Retrieve related **OfferItem_Offer** via Association from **$IteratorOfferItem** (Result: **$Offer_2**)**
   │ 2. **Add **$$Offer_2
** to/from list **$OfferList****
   │ 3. **List Operation: **FilterByExpression** on **$undefined** where `$currentObject/EcoATM_PWS.OfferItem_Device=$IteratorOfferItem/EcoATM_PWS.OfferItem_Device` (Result: **$OfferItemList_Count**)**
   │ 4. **AggregateList**
   │ 5. 🔀 **DECISION:** `$Count>1`
   │    ➔ **If [true]:**
   │       1. **Create Variable **$Sum** = `0`**
   │       2. 🔄 **LOOP:** For each **$IteratorOfferItem_2** in **$OfferItemList_Count**
   │          │ 1. 🔀 **DECISION:** `$IteratorOfferItem_2/SalesOfferItemStatus = EcoATM_PWS.ENUM_OfferItemStatus.Accept`
   │          │    ➔ **If [true]:**
   │          │       1. **Update Variable **$Sum** = `$Sum + $IteratorOfferItem_2/OfferQuantity`**
   │          │    ➔ **If [false]:**
   │          │       1. 🔀 **DECISION:** `$IteratorOfferItem_2/SalesOfferItemStatus = EcoATM_PWS.ENUM_OfferItemStatus.Counter or $IteratorOfferItem_2/SalesOfferItemStatus = EcoATM_PWS.ENUM_OfferItemStatus.Finalize`
   │          │          ➔ **If [true]:**
   │          │             1. **Update Variable **$Sum** = `if $IteratorOfferItem_2/CounterQuantity!=empty then $Sum + $IteratorOfferItem_2/CounterQuantity else $Sum + $IteratorOfferItem_2/OfferQuantity`**
   │          │          ➔ **If [false]:**
   │          └─ **End Loop**
   │       3. 🔀 **DECISION:** `$Sum > $IteratorOfferItem/EcoATM_PWS.OfferItem_Device/EcoATM_PWSMDM.Device/AvailableQty`
   │          ➔ **If [true]:**
   │             1. **Update **$IteratorOfferItem**
      - Set **SameSKUOfferAvailable** = `true`**
   │          ➔ **If [false]:**
   │             1. **Update **$IteratorOfferItem**
      - Set **SameSKUOfferAvailable** = `false`**
   │    ➔ **If [false]:**
   │       1. **Update **$IteratorOfferItem**
      - Set **SameSKUOfferAvailable** = `false`**
   └─ **End Loop**
8. **Commit/Save **$OfferItemList_Complete** to Database**
9. 🔄 **LOOP:** For each **$IteratorOffer** in **$OfferList**
   │ 1. **Retrieve related **OfferItem_Offer** via Association from **$IteratorOffer** (Result: **$OfferItemList_2**)**
   │ 2. **List Operation: **Find** on **$undefined** where `true` (Result: **$Offer_AtleastOneItemMarked**)**
   │ 3. 🔀 **DECISION:** `$Offer_AtleastOneItemMarked!=empty`
   │    ➔ **If [false]:**
   │       1. **Update **$IteratorOffer**
      - Set **SameSKUOffer** = `false`**
   │    ➔ **If [true]:**
   │       1. **Update **$IteratorOffer**
      - Set **SameSKUOffer** = `true`**
   └─ **End Loop**
10. **Commit/Save **$OfferList** to Database**
11. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
12. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.