# Microflow Detailed Specification: SUB_CreateModelNameList

### 📥 Inputs (Parameters)
- **$Week** (Type: EcoATM_MDM.Week)
- **$ModelNameList** (Type: EcoATM_MDM.ModelName)
- **$enum_BuyerCodeType** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'CreateModelNameFilters'`**
2. **Create Variable **$Message** = `'Create Wholesale Auction ModelName Filters'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **Create Variable **$OQLQuery_AggregatedInventoryFilter** = `empty`**
5. 🔀 **DECISION:** `$enum_BuyerCodeType = AuctionUI.enum_BuyerCodeType.Wholesale`
   ➔ **If [true]:**
      1. **Update Variable **$OQLQuery_AggregatedInventoryFilter** = `'select Name as ModelName from AuctionUI."AggregatedInventory" where AuctionUI.AggregatedInventory/AuctionUI.AggregatedInventory_Week/EcoATM_MDM."Week"/WeekID = ' + toString($Week/WeekID) + ' group by ModelName'`**
      2. **JavaCallAction**
      3. 🔄 **LOOP:** For each **$IteratorHelper** in **$ModelNameHelperList**
         │ 1. **Call Microflow **EcoATM_BuyerManagement.SUB_GetOrCreateModelName** (Result: **$Model**)**
         └─ **End Loop**
      4. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      5. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update Variable **$OQLQuery_AggregatedInventoryFilter** = `'select Name as ModelName from AuctionUI."AggregatedInventory" where AuctionUI.AggregatedInventory/AuctionUI.AggregatedInventory_Week/EcoATM_MDM."Week"/WeekID = ' + toString($Week/WeekID) + ' and DWTotalQuantity > 0 group by ModelName'`**
      2. **JavaCallAction**
      3. 🔄 **LOOP:** For each **$IteratorHelper** in **$ModelNameHelperList**
         │ 1. **Call Microflow **EcoATM_BuyerManagement.SUB_GetOrCreateModelName** (Result: **$Model**)**
         └─ **End Loop**
      4. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.