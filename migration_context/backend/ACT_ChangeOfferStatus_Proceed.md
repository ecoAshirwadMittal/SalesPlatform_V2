# Microflow Detailed Specification: ACT_ChangeOfferStatus_Proceed

### 📥 Inputs (Parameters)
- **$ChangeOfferStatusHelper** (Type: EcoATM_PWS.ChangeOfferStatusHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **Call Microflow **EcoATM_PWS.VAL_ChargeOfferStatusHelper_IsValid** (Result: **$IsValid**)**
3. 🔀 **DECISION:** `$IsValid`
   ➔ **If [true]:**
      1. **Call Microflow **EcoATM_PWS.SUB_ChangeOfferStatus_GetOrderList** (Result: **$OrderList**)**
      2. 🔀 **DECISION:** `$OrderList!=empty`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$ChangeOfferStatusHelper/NotOrderStatusChange`
               ➔ **If [true]:**
                  1. 🔄 **LOOP:** For each **$IteratorOrder2** in **$OrderList**
                     │ 1. **Update **$IteratorOrder2**
      - Set **HasShipmentDetails** = `$ChangeOfferStatusHelper/HasShipmentDetails`
      - Set **LegacyOrder** = `$IteratorOrder2/LegacyOrder`**
                     └─ **End Loop**
                  2. **Commit/Save **$OrderList** to Database**
                  3. **AggregateList**
                  4. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                  5. **Close current page/popup**
                  6. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$ChangeOfferStatusHelper/AllPeriod`
                     ➔ **If [false]:**
                        1. **CreateList**
                        2. 🔄 **LOOP:** For each **$IteratorOrder** in **$OrderList**
                           │ 1. **Retrieve related **Offer_Order** via Association from **$IteratorOrder** (Result: **$Offer**)**
                           │ 2. 🔀 **DECISION:** `$Offer/EcoATM_PWS.Offer_OrderStatus/EcoATM_PWS.OrderStatus/SystemStatus=getCaption($ChangeOfferStatusHelper/FromOfferStatus)`
                           │    ➔ **If [true]:**
                           │       1. **Update **$Offer**
      - Set **Offer_OrderStatus** = `$ChangeOfferStatusHelper/EcoATM_PWS.ChangeOfferStatusHelper_OrderStatus`**
                           │       2. **Add **$$Offer
** to/from list **$OfferToCommitList****
                           │    ➔ **If [false]:**
                           └─ **End Loop**
                        3. **Commit/Save **$OfferToCommitList** to Database**
                        4. 🔄 **LOOP:** For each **$IteratorOrder2** in **$OrderList**
                           │ 1. **Update **$IteratorOrder2**
      - Set **HasShipmentDetails** = `$ChangeOfferStatusHelper/HasShipmentDetails`
      - Set **LegacyOrder** = `$IteratorOrder2/LegacyOrder`**
                           └─ **End Loop**
                        5. **Commit/Save **$OrderList** to Database**
                        6. **AggregateList**
                        7. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                        8. **Close current page/popup**
                        9. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **CreateList**
                        2. 🔄 **LOOP:** For each **$IteratorOrder_1** in **$OrderList**
                           │ 1. **Retrieve related **Offer_Order** via Association from **$IteratorOrder_1** (Result: **$Offer_1**)**
                           │ 2. **Update **$Offer_1**
      - Set **Offer_OrderStatus** = `$ChangeOfferStatusHelper/EcoATM_PWS.ChangeOfferStatusHelper_OrderStatus`**
                           │ 3. **Add **$$Offer_1
** to/from list **$OfferToCommitList_1****
                           └─ **End Loop**
                        3. **Commit/Save **$OfferToCommitList_1** to Database**
                        4. 🔄 **LOOP:** For each **$IteratorOrder2** in **$OrderList**
                           │ 1. **Update **$IteratorOrder2**
      - Set **HasShipmentDetails** = `$ChangeOfferStatusHelper/HasShipmentDetails`
      - Set **LegacyOrder** = `$IteratorOrder2/LegacyOrder`**
                           └─ **End Loop**
                        5. **Commit/Save **$OrderList** to Database**
                        6. **AggregateList**
                        7. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                        8. **Close current page/popup**
                        9. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            2. **Close current page/popup**
            3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.