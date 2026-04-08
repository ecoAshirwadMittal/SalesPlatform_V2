# Microflow Detailed Specification: SUB_LoadAggregatedInventory

### 📥 Inputs (Parameters)
- **$Week** (Type: EcoATM_MDM.Week)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **Retrieve related **AggInventoryTotals_Week** via Association from **$Week** (Result: **$AggreegatedInventoryTotals**)**
3. **Retrieve related **AggregatedInventory_Week** via Association from **$Week** (Result: **$AggregateInventoryList**)**
4. **Delete**
5. **Delete**
6. **Call Microflow **Custom_Logging.SUB_Log_Info****
7. **Create Variable **$PageSize** = `@EcoATM_Integration.CONST_SF_QueryPageSize`**
8. **Create Variable **$PageNumber** = `1`**
9. **Create Variable **$OffSet** = `0`**
10. **Update Variable **$OffSet** = `($PageNumber-1)*$PageSize`**
11. **ExecuteDatabaseQuery**
12. **Call Microflow **Custom_Logging.SUB_Log_Info****
13. 🔀 **DECISION:** `$AggreegatedInventory !=empty`
   ➔ **If [true]:**
      1. **ExportXml**
      2. **ImportXml**
      3. **Call Microflow **Custom_Logging.SUB_Log_Info****
      4. **Update Variable **$PageNumber** = `$PageNumber+1`**
         *(Merging with existing path logic)*
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Info****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.