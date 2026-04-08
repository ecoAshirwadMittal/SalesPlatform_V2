# Microflow Detailed Specification: SUB_UpdatePOFromPackOut

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
                  1. **ExecuteDatabaseQuery**
                  2. 🔀 **DECISION:** `$DIM_PACKOUT != empty`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **CreateList**
                        2. **CreateList**
                        3. **Retrieve related **PurchaseOrder_Week_From** via Association from **$PurchaseOrder** (Result: **$StartWeek**)**
                        4. **DB Retrieve **EcoATM_MDM.Week** Filter: `[WeekStartDateTime>=$StartWeek/WeekStartDateTime and WeekEndDateTime < $Last_Week/WeekEndDateTime]` (Result: **$WeekList_Complete**)**
                        5. 🔄 **LOOP:** For each **$IteratorVW_SALE_ORDER_PO** in **$DIM_PACKOUT**
                           │ 1. **DB Retrieve **EcoATM_PO.PODetail** Filter: `[EcoATM_PO.PODetail_PurchaseOrder = $PurchaseOrder] [ProductID = $IteratorVW_SALE_ORDER_PO/ECOATM_CODE] [Grade = $IteratorVW_SALE_ORDER_PO/GRADE] [EcoATM_PO.PODetail_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code= $IteratorVW_SALE_ORDER_PO/BUYER_CODE]` (Result: **$PODetail**)**
                           │ 2. 🔀 **DECISION:** `$PODetail != empty`
                           │    ➔ **If [true]:**
                           │       1. **Retrieve related **WeeklyPO_PODetail** via Association from **$PODetail** (Result: **$WeeklyPackOutList**)**
                           │       2. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoATM_PO.WeeklyPO_Week = $Last_Week` (Result: **$WeeklyPOFulfillmentFound**)**
                           │       3. **Create Variable **$Agg_Qty** = `0`**
                           │       4. **Create Variable **$Agg_Revenue** = `0`**
                           │       5. 🔄 **LOOP:** For each **$IteratorWeeksPO** in **$WeekList_Complete**
                           │          │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoATM_PO.WeeklyPO_Week= $IteratorWeeksPO` (Result: **$CurrentIteratorWeeklyPO**)**
                           │          │ 2. 🔀 **DECISION:** `$CurrentIteratorWeeklyPO!=empty`
                           │          │    ➔ **If [true]:**
                           │          │       1. **Update Variable **$Agg_Qty** = `$Agg_Qty+ (if $CurrentIteratorWeeklyPO/Qty!=empty then $CurrentIteratorWeeklyPO/Qty else 0)`**
                           │          │       2. **Update Variable **$Agg_Revenue** = `if $CurrentIteratorWeeklyPO/Qty!=empty and $CurrentIteratorWeeklyPO/Price!=empty then ($CurrentIteratorWeeklyPO/Qty* $CurrentIteratorWeeklyPO/Price)+ $Agg_Revenue else $Agg_Revenue`**
                           │          │    ➔ **If [false]:**
                           │          └─ **End Loop**
                           │       6. **Update Variable **$Agg_Qty** = `$Agg_Qty+ (if $IteratorVW_SALE_ORDER_PO/SHIPPED_QUANTITY!=empty then $IteratorVW_SALE_ORDER_PO/SHIPPED_QUANTITY else 0)`**
                           │       7. **Update Variable **$Agg_Revenue** = `if $IteratorVW_SALE_ORDER_PO/SHIPPED_QUANTITY!=empty and $IteratorVW_SALE_ORDER_PO/UNIT_SELLING_PRICE!=empty then ($IteratorVW_SALE_ORDER_PO/SHIPPED_QUANTITY*$IteratorVW_SALE_ORDER_PO/UNIT_SELLING_PRICE)+ $Agg_Revenue else $Agg_Revenue`**
                           │       8. 🔀 **DECISION:** `$WeeklyPOFulfillmentFound !=empty`
                           │          ➔ **If [true]:**
                           │             1. **Update **$WeeklyPOFulfillmentFound**
      - Set **Qty** = `$IteratorVW_SALE_ORDER_PO/SHIPPED_QUANTITY`
      - Set **Price** = `$IteratorVW_SALE_ORDER_PO/UNIT_SELLING_PRICE`**
                           │             2. **Add **$$WeeklyPOFulfillmentFound
** to/from list **$WeeklyPOList_ToBeCommited****
                           │             3. **Update **$PODetail**
      - Set **QtyFullfiled** = `$Agg_Qty`
      - Set **PriceFulfilled** = `if $Agg_Revenue!=0 and $Agg_Qty!=0 then $Agg_Revenue: $Agg_Qty else 0`**
                           │             4. **Add **$$PODetail
** to/from list **$PODetailList_ToBeCommited****
                           │          ➔ **If [false]:**
                           │             1. **Create **EcoATM_PO.WeeklyPO** (Result: **$NewWeeklyPackOut**)
      - Set **Qty** = `$IteratorVW_SALE_ORDER_PO/SHIPPED_QUANTITY`
      - Set **Price** = `$IteratorVW_SALE_ORDER_PO/UNIT_SELLING_PRICE`
      - Set **WeeklyPO_PODetail** = `$PODetail`
      - Set **WeeklyPO_PurchaseOrder** = `$PurchaseOrder`
      - Set **WeeklyPO_Week** = `$Last_Week`**
                           │             2. **Add **$$NewWeeklyPackOut
** to/from list **$WeeklyPOList_ToBeCommited****
                           │             3. **Update **$PODetail**
      - Set **QtyFullfiled** = `$Agg_Qty`
      - Set **PriceFulfilled** = `if $Agg_Revenue!=0 and $Agg_Qty!=0 then $Agg_Revenue: $Agg_Qty else 0`**
                           │             4. **Add **$$PODetail
** to/from list **$PODetailList_ToBeCommited****
                           │    ➔ **If [false]:**
                           └─ **End Loop**
                        6. **Commit/Save **$WeeklyPOList_ToBeCommited** to Database**
                        7. **Commit/Save **$PODetailList_ToBeCommited** to Database**
                        8. **Update **$PurchaseOrder** (and Save to DB)
      - Set **PORefreshTimeStamp** = `[%CurrentDateTime%]`**
                        9. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.