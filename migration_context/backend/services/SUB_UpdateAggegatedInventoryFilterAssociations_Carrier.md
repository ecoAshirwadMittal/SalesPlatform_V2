# Microflow Detailed Specification: SUB_UpdateAggegatedInventoryFilterAssociations_Carrier

### 📥 Inputs (Parameters)
- **$AggregatedInventory** (Type: AuctionUI.AggregatedInventory)
- **$CarrierList** (Type: EcoATM_MDM.Carrier)
- **$enum_BuyerCodeType** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$Message** = `'Update Aggregated Inventory Filter Carrier'`**
2. **Create Variable **$TimerName** = `'UpdateAggregatedInventoryFilterCarrier'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **Call Microflow **EcoATM_BuyerManagement.SUB_GetOrCreateCarrier** (Result: **$Carrier**)**
5. 🔀 **DECISION:** `$Carrier != empty`
   ➔ **If [true]:**
      1. **Update **$Carrier**
      - Set **IsEnabledForFilter** = `true`**
      2. **Update **$AggregatedInventory**
      - Set **AggregatedInventory_Carrier** = `$Carrier`**
      3. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      4. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.