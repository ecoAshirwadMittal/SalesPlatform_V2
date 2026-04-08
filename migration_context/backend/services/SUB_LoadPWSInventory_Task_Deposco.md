# Microflow Detailed Specification: SUB_LoadPWSInventory_Task_Deposco

### 📥 Inputs (Parameters)
- **$PageNo** (Type: Variable)
- **$ItemInventoryItemList** (Type: EcoATM_PWSIntegration.ItemInventoryItem)
- **$FullInventorySync** (Type: Variable)
- **$DeposcoConfig** (Type: EcoATM_PWSIntegration.DeposcoConfig)
- **$DesposcoAPI** (Type: EcoATM_PWSIntegration.DesposcoAPIs)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'PWSInventorySyncTask_Deposco'`**
2. **Create Variable **$Description** = `'PWS Inventory Sync Deposco API . PageNum: ' + $PageNo`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **Call Microflow **EcoATM_PWSIntegration.SUB_FetchItemsFromDeposco****
5. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
6. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.