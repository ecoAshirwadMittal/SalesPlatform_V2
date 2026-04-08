# Microflow Detailed Specification: SUB_CreateCarrierList

### 📥 Inputs (Parameters)
- **$Week** (Type: EcoATM_MDM.Week)
- **$CarrierList** (Type: EcoATM_MDM.Carrier)
- **$enum_BuyerCodeType** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'CreateCarrierFilters'`**
2. **Create Variable **$Message** = `'Create Wholesale Auction Carrier Filters'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **Create Variable **$OQLQuery_AggregatedInventoryFilter** = `empty`**
5. 🔀 **DECISION:** `$enum_BuyerCodeType = AuctionUI.enum_BuyerCodeType.Wholesale`
   ➔ **If [true]:**
      1. **Update Variable **$OQLQuery_AggregatedInventoryFilter** = `'select Carrier as Carrier from AuctionUI."AggregatedInventory" where AuctionUI.AggregatedInventory/AuctionUI.AggregatedInventory_Week/EcoATM_MDM."Week"/WeekID = ' + toString($Week/WeekID) + ' group by Carrier'`**
      2. **JavaCallAction**
      3. 🔄 **LOOP:** For each **$IteratorHelper** in **$CarrierHelperList**
         │ 1. **Call Microflow **EcoATM_BuyerManagement.SUB_GetOrCreateCarrier** (Result: **$Model**)**
         └─ **End Loop**
      4. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      5. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update Variable **$OQLQuery_AggregatedInventoryFilter** = `'select Carrier as Carrier from AuctionUI."AggregatedInventory" where AuctionUI.AggregatedInventory/AuctionUI.AggregatedInventory_Week/EcoATM_MDM."Week"/WeekID = ' + toString($Week/WeekID) + ' and DWTotalQuantity > 0 group by Carrier'`**
      2. **JavaCallAction**
      3. 🔄 **LOOP:** For each **$IteratorHelper** in **$CarrierHelperList**
         │ 1. **Call Microflow **EcoATM_BuyerManagement.SUB_GetOrCreateCarrier** (Result: **$Model**)**
         └─ **End Loop**
      4. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.