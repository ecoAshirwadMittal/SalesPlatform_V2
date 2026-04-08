# Microflow Analysis: SUB_LoadPWSInventory_Task_Deposco_Test

### Requirements (Inputs):
- **$PageNo** (A record of type: Object)
- **$DeposcoConfig** (A record of type: EcoATM_PWSIntegration.DeposcoConfig)
- **$DesposcoAPI** (A record of type: EcoATM_PWSIntegration.DesposcoAPIs)
- **$ItemInventoryItem_TestList** (A record of type: EcoATM_PWSIntegration.ItemInventoryItem_Test)
- **$FacilityInventoryItem_TestList** (A record of type: EcoATM_PWSIntegration.FacilityInventoryItem_Test)
- **$Inventory_TestList** (A record of type: EcoATM_PWSIntegration.Inventory_Test)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Run another process: "EcoATM_PWSIntegration.SUB_FetchItemsFromDeposco_Test"** ⚠️ *(This step has a safety catch if it fails)*
5. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
