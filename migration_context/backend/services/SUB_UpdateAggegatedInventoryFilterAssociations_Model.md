# Microflow Detailed Specification: SUB_UpdateAggegatedInventoryFilterAssociations_Model

### 📥 Inputs (Parameters)
- **$AggregatedInventory** (Type: AuctionUI.AggregatedInventory)
- **$ModelList** (Type: EcoATM_MDM.Model)
- **$enum_BuyerCodeType** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$Message** = `'Update Aggregated Inventory Filter Model'`**
2. **Create Variable **$TimerName** = `'UpdateAggregatedInventoryFilterModel'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **Call Microflow **EcoATM_BuyerManagement.SUB_GetOrCreateModel** (Result: **$Model**)**
5. 🔀 **DECISION:** `$Model != empty`
   ➔ **If [true]:**
      1. **Update **$Model**
      - Set **IsEnabledForFilter** = `true`**
      2. **Update **$AggregatedInventory**
      - Set **AggregatedInventory_Model** = `$Model`**
      3. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      4. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.