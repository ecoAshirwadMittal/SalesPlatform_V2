# Microflow Detailed Specification: SUB_CreateModelList

### 📥 Inputs (Parameters)
- **$Week** (Type: EcoATM_MDM.Week)
- **$ModelList** (Type: EcoATM_MDM.Model)
- **$enum_BuyerCodeType** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'CreateModelFilters'`**
2. **Create Variable **$Message** = `'Create Wholesale Auction Model Filters'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **Create Variable **$OQLQuery_AggregatedInventoryFilter** = `empty`**
5. 🔀 **DECISION:** `$enum_BuyerCodeType = AuctionUI.enum_BuyerCodeType.Wholesale`
   ➔ **If [true]:**
      1. **Update Variable **$OQLQuery_AggregatedInventoryFilter** = `'select Model as Model from AuctionUI."AggregatedInventory" where AuctionUI.AggregatedInventory/AuctionUI.AggregatedInventory_Week/EcoATM_MDM."Week"/WeekID = ' + toString($Week/WeekID) + ' group by Model'`**
      2. **JavaCallAction**
      3. 🔄 **LOOP:** For each **$IteratorHelper** in **$ModelHelperList**
         │ 1. **Call Microflow **EcoATM_BuyerManagement.SUB_GetOrCreateModel** (Result: **$Model**)**
         └─ **End Loop**
      4. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      5. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update Variable **$OQLQuery_AggregatedInventoryFilter** = `'select Model as Model from AuctionUI."AggregatedInventory" where AuctionUI.AggregatedInventory/AuctionUI.AggregatedInventory_Week/EcoATM_MDM."Week"/WeekID = ' + toString($Week/WeekID) + ' and DWTotalQuantity > 0 group by Model'`**
      2. **JavaCallAction**
      3. 🔄 **LOOP:** For each **$IteratorHelper** in **$ModelHelperList**
         │ 1. **Call Microflow **EcoATM_BuyerManagement.SUB_GetOrCreateModel** (Result: **$Model**)**
         └─ **End Loop**
      4. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.