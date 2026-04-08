# Microflow Analysis: SUB_FetchItemsFromDeposco

### Requirements (Inputs):
- **$PageNo** (A record of type: Object)
- **$ItemInventoryItemList** (A record of type: EcoATM_PWSIntegration.ItemInventoryItem)
- **$FullInventorySync** (A record of type: Object)
- **$DeposcoConfig** (A record of type: EcoATM_PWSIntegration.DeposcoConfig)
- **$DesposcoAPI** (A record of type: EcoATM_PWSIntegration.DesposcoAPIs)

### Execution Steps:
1. **Create Variable**
2. **Run another process: "EcoATM_PWSIntegration.SUB_GenerateDeposcoPassword"
      - Store the result in a new variable called **$EncodedAuth****
3. **Create Variable**
4. **Rest Call** ⚠️ *(This step has a safety catch if it fails)*
5. **Run another process: "EcoATM_PWSIntegration.ACT_AuditRestAPICalls"**
6. **Import Xml**
7. **Aggregate List
      - Store the result in a new variable called **$ResponseCount****
8. **Run another process: "Custom_Logging.SUB_Log_Info"**
9. **Change List**
10. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
