# Microflow Detailed Specification: SUB_CreateBrandList

### 📥 Inputs (Parameters)
- **$Week** (Type: EcoATM_MDM.Week)
- **$BrandList** (Type: EcoATM_MDM.Brand)
- **$enum_BuyerCodeType** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'CreateBrandFilters'`**
2. **Create Variable **$Message** = `'Create Wholesale Auction Brand Filters'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **Create Variable **$OQLQuery_AggregatedInventoryBrand** = `empty`**
5. 🔀 **DECISION:** `$enum_BuyerCodeType = AuctionUI.enum_BuyerCodeType.Wholesale`
   ➔ **If [true]:**
      1. **Update Variable **$OQLQuery_AggregatedInventoryBrand** = `'select Brand as Brand from AuctionUI."AggregatedInventory" where AuctionUI.AggregatedInventory/AuctionUI.AggregatedInventory_Week/EcoATM_MDM."Week"/WeekID = ' + toString($Week/WeekID) + ' group by Brand'`**
      2. **JavaCallAction**
      3. 🔄 **LOOP:** For each **$IteratorBrandHelper** in **$BrandHelperList**
         │ 1. **Call Microflow **EcoATM_BuyerManagement.SUB_GetOrCreateBrand** (Result: **$Brand**)**
         └─ **End Loop**
      4. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      5. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update Variable **$OQLQuery_AggregatedInventoryBrand** = `'select Brand as Brand from AuctionUI."AggregatedInventory" where AuctionUI.AggregatedInventory/AuctionUI.AggregatedInventory_Week/EcoATM_MDM."Week"/WeekID = ' + toString($Week/WeekID) + ' and DWTotalQuantity > 0 group by Brand'`**
      2. **JavaCallAction**
      3. 🔄 **LOOP:** For each **$IteratorBrandHelper** in **$BrandHelperList**
         │ 1. **Call Microflow **EcoATM_BuyerManagement.SUB_GetOrCreateBrand** (Result: **$Brand**)**
         └─ **End Loop**
      4. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.