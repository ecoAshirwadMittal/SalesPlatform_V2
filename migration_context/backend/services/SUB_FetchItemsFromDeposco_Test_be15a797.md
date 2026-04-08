# Microflow Analysis: SUB_FetchItemsFromDeposco_Test

### Requirements (Inputs):
- **$PageNo** (A record of type: Object)
- **$DeposcoConfig** (A record of type: EcoATM_PWSIntegration.DeposcoConfig)
- **$DesposcoAPI** (A record of type: EcoATM_PWSIntegration.DesposcoAPIs)
- **$ItemInventoryItem_TestList** (A record of type: EcoATM_PWSIntegration.ItemInventoryItem_Test)
- **$FacilityInventoryItem_TestList** (A record of type: EcoATM_PWSIntegration.FacilityInventoryItem_Test)
- **$Inventory_TestList** (A record of type: EcoATM_PWSIntegration.Inventory_Test)

### Execution Steps:
1. **Create Variable**
2. **Run another process: "EcoATM_PWSIntegration.SUB_GenerateDeposcoPassword"
      - Store the result in a new variable called **$EncodedAuth****
3. **Create Variable**
4. **Rest Call** ⚠️ *(This step has a safety catch if it fails)*
5. **Run another process: "EcoATM_PWSIntegration.ACT_AuditRestAPICalls"**
6. **Import Xml**
7. **Change List**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
