# Microflow Detailed Specification: SUB_MarkLegacyOrders

### 📥 Inputs (Parameters)
- **$LegacyOrderDateHelper** (Type: EcoATM_PWS.LegacyOrderDateHelper)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'MarkLegacyOrdersShipped'`**
2. **Create Variable **$Description** = `'Marking Legacy Orders as Ship Complete'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. 🔀 **DECISION:** `$LegacyOrderDateHelper/LegacyDate!=empty`
   ➔ **If [true]:**
      1. **DB Retrieve **EcoATM_PWS.Order**  (Result: **$OrderList**)**
      2. 🔀 **DECISION:** `$OrderList!=empty`
         ➔ **If [true]:**
            1. **List Operation: **FilterByExpression** on **$undefined** where `trimToDays($currentObject/createdDate) <= trimToDays($LegacyOrderDateHelper/LegacyDate)` (Result: **$OrderList_filtered**)**
            2. 🔀 **DECISION:** `$OrderList_filtered!=empty`
               ➔ **If [true]:**
                  1. **DB Retrieve **EcoATM_PWS.OrderStatus** Filter: `[SystemStatus ='Shipped']` (Result: **$OrderStatus_Shipped**)**
                  2. 🔄 **LOOP:** For each **$IteratorOrder** in **$OrderList_filtered**
                     │ 1. **Update **$IteratorOrder**
      - Set **HasShipmentDetails** = `true`
      - Set **LegacyOrder** = `true`
      - Set **Order_OrderStatus** = `$OrderStatus_Shipped`**
                     └─ **End Loop**
                  3. **Commit/Save **$OrderList** to Database**
                  4. **Close current page/popup**
                  5. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                  6. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **ValidationFeedback**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.