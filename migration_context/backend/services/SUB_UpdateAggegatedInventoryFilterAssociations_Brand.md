# Microflow Detailed Specification: SUB_UpdateAggegatedInventoryFilterAssociations_Brand

### 📥 Inputs (Parameters)
- **$AggregatedInventory** (Type: AuctionUI.AggregatedInventory)
- **$BrandList** (Type: EcoATM_MDM.Brand)
- **$enum_BuyerCodeType** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$Message** = `'Update Aggregated Inventory Filter - Brand'`**
2. **Create Variable **$TimerName** = `'UpdateAggregatedInventoryFilterBrand'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **Call Microflow **EcoATM_BuyerManagement.SUB_GetOrCreateBrand** (Result: **$Brand**)**
5. 🔀 **DECISION:** `$Brand != empty`
   ➔ **If [true]:**
      1. **Update **$Brand**
      - Set **IsEnabledForFilter** = `true`**
      2. **Update **$AggregatedInventory**
      - Set **AggregatedInventory_Brand** = `$Brand`**
      3. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      4. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.