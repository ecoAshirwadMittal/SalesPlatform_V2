# Microflow Detailed Specification: ACT_OnDemandSync

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_MDM.Week** Filter: `[WeekStartDateTime<='[%CurrentDateTime%]' and WeekEndDateTime >'[%CurrentDateTime%]']` (Result: **$Week**)**
2. **Call Microflow **EcoATM_BidData.SUB_GetCurrentWeekMinusOne** (Result: **$Last_Week**)**
3. 🔀 **DECISION:** `$Week != empty`
   ➔ **If [true]:**
      1. **DB Retrieve **EcoATM_PO.PurchaseOrder** Filter: `[EcoATM_PO.PurchaseOrder_Week_From/EcoATM_MDM.Week/WeekStartDateTime <= $Last_Week/WeekStartDateTime and EcoATM_PO.PurchaseOrder_Week_To/EcoATM_MDM.Week/WeekEndDateTime >= $Last_Week/WeekEndDateTime]` (Result: **$PurchaseOrder**)**
      2. 🔀 **DECISION:** `$PurchaseOrder != empty`
         ➔ **If [true]:**
            1. **Retrieve related **PODetail_PurchaseOrder** via Association from **$PurchaseOrder** (Result: **$PODetailList**)**
            2. 🔀 **DECISION:** `$PODetailList != empty`
               ➔ **If [false]:**
                  1. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **Retrieve related **PurchaseOrder_Week_From** via Association from **$PurchaseOrder** (Result: **$StartWeek**)**
                  2. **Retrieve related **PurchaseOrder_Week_To** via Association from **$PurchaseOrder** (Result: **$EndWeek**)**
                  3. **DB Retrieve **EcoATM_MDM.Week** Filter: `[WeekStartDateTime>=$StartWeek/WeekStartDateTime and WeekEndDateTime <= $Last_Week/WeekEndDateTime]` (Result: **$WeekList_PO**)**
                  4. **CreateList**
                  5. **CreateList**
                  6. **Create Variable **$SnowflakeError** = `false`**
                  7. **Create Variable **$SnowflakeErrorWeek** = `0`**
                  8. 🔄 **LOOP:** For each **$IteratorWeek** in **$WeekList_PO**
                     │ 1. **ExecuteDatabaseQuery**
                     │ 2. 🔀 **DECISION:** `$VW_SALE_ORDER_PO != empty`
                     │    ➔ **If [true]:**
                     │       1. 🔄 **LOOP:** For each **$IteratorVW_SALE_ORDER_PO** in **$VW_SALE_ORDER_PO**
                     │          │ 1. **DB Retrieve **EcoATM_PO.PODetail** Filter: `[EcoATM_PO.PODetail_PurchaseOrder = $PurchaseOrder] [ProductID = $IteratorVW_SALE_ORDER_PO/ECOATM_CODE] [Grade = $IteratorVW_SALE_ORDER_PO/GRADE] [EcoATM_PO.PODetail_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code= $IteratorVW_SALE_ORDER_PO/BUYER_CODE]` (Result: **$PODetail**)**
                     │          │ 2. 🔀 **DECISION:** `$PODetail != empty`
                     │          │    ➔ **If [true]:**
                     │          │       1. **Retrieve related **WeeklyPO_PODetail** via Association from **$PODetail** (Result: **$WeeklyPackOutList**)**
                     │          │       2. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoATM_PO.WeeklyPO_Week = $IteratorWeek` (Result: **$WeeklyPOFulfillmentFound_Week**)**
                     │          │       3. 🔀 **DECISION:** `$WeeklyPOFulfillmentFound_Week!=empty`
                     │          │          ➔ **If [true]:**
                     │          │             1. **Update **$WeeklyPOFulfillmentFound_Week**
      - Set **Qty** = `$IteratorVW_SALE_ORDER_PO/SHIPPED_QUANTITY`
      - Set **Price** = `$IteratorVW_SALE_ORDER_PO/UNIT_SELLING_PRICE`
      - Set **WeeklyPO_Week** = `$IteratorWeek`**
                     │          │             2. **Add **$$WeeklyPOFulfillmentFound_Week
** to/from list **$WeeklyPOList_ToBeCommited****
                     │          │          ➔ **If [false]:**
                     │          │             1. **Create **EcoATM_PO.WeeklyPO** (Result: **$NewWeeklyPackOut**)
      - Set **Qty** = `$IteratorVW_SALE_ORDER_PO/SHIPPED_QUANTITY`
      - Set **Price** = `$IteratorVW_SALE_ORDER_PO/UNIT_SELLING_PRICE`
      - Set **WeeklyPO_PODetail** = `$PODetail`
      - Set **WeeklyPO_PurchaseOrder** = `$PurchaseOrder`
      - Set **WeeklyPO_Week** = `$IteratorWeek`**
                     │          │             2. **Add **$$NewWeeklyPackOut
** to/from list **$WeeklyPOList_ToBeCommited****
                     │          │    ➔ **If [false]:**
                     │          └─ **End Loop**
                     │    ➔ **If [false]:**
                     │       1. 🔀 **DECISION:** `$Last_Week=$IteratorWeek`
                     │          ➔ **If [false]:**
                     │             1. **Update Variable **$SnowflakeError** = `true`**
                     │             2. **Update Variable **$SnowflakeErrorWeek** = `$IteratorWeek/WeekNumber`**
                     │          ➔ **If [true]:**
                     └─ **End Loop**
                  9. 🔀 **DECISION:** `$SnowflakeError`
                     ➔ **If [false]:**
                        1. **Commit/Save **$WeeklyPOList_ToBeCommited** to Database**
                        2. **DB Retrieve **EcoATM_PO.PODetail** Filter: `[EcoATM_PO.PODetail_PurchaseOrder = $PurchaseOrder]` (Result: **$PODetailList_QPF**)**
                        3. 🔄 **LOOP:** For each **$IteratorPODetail** in **$PODetailList_QPF**
                           │ 1. **Create Variable **$Agg_Qty** = `0`**
                           │ 2. **Create Variable **$Agg_Revenue** = `0`**
                           │ 3. **Retrieve related **WeeklyPO_PODetail** via Association from **$IteratorPODetail** (Result: **$WeeklyPackOutList_QPF**)**
                           │ 4. 🔄 **LOOP:** For each **$IteratorWeek_QPF** in **$WeekList_PO**
                           │    │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoATM_PO.WeeklyPO_Week = $IteratorWeek_QPF` (Result: **$WeeklyPOFulfillmentFound_Week_QPF**)**
                           │    │ 2. 🔀 **DECISION:** `$WeeklyPOFulfillmentFound_Week_QPF!=empty`
                           │    │    ➔ **If [true]:**
                           │    │       1. **Update Variable **$Agg_Qty** = `$Agg_Qty+ (if $WeeklyPOFulfillmentFound_Week_QPF/Qty!=empty then $WeeklyPOFulfillmentFound_Week_QPF/Qty else 0)`**
                           │    │       2. **Update Variable **$Agg_Revenue** = `if $WeeklyPOFulfillmentFound_Week_QPF/Qty!=empty and $WeeklyPOFulfillmentFound_Week_QPF/Price!=empty then $WeeklyPOFulfillmentFound_Week_QPF/Qty* $WeeklyPOFulfillmentFound_Week_QPF/Price+ $Agg_Revenue else $Agg_Revenue`**
                           │    │    ➔ **If [false]:**
                           │    └─ **End Loop**
                           │ 5. **Update **$IteratorPODetail**
      - Set **QtyFullfiled** = `$Agg_Qty`
      - Set **PriceFulfilled** = `if $Agg_Revenue!=0 and $Agg_Qty!=0 then $Agg_Revenue: $Agg_Qty else 0`**
                           │ 6. **Add **$$IteratorPODetail
** to/from list **$PODetailList_ToBeCommited****
                           └─ **End Loop**
                        4. **Commit/Save **$PODetailList_ToBeCommited** to Database**
                        5. **Show Message (Information): `Sync complete`**
                        6. **Update **$PurchaseOrder** (and Save to DB)
      - Set **PORefreshTimeStamp** = `[%CurrentDateTime%]`**
                        7. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Show Message (Information): `Snowflake Error:Missing data for Week {1}`**
                        2. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.