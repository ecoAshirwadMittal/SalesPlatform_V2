# Microflow Analysis: SUB_SyncCaseLots

### Requirements (Inputs):
- **$EncodedAuth** (A record of type: Object)
- **$PageNo** (A record of type: Object)
- **$StockUnitItemListAggregated** (A record of type: EcoATM_PWSIntegration.StockUnitItems)
- **$DeposcoConfig** (A record of type: EcoATM_PWSIntegration.DeposcoConfig)
- **$DesposcoAPI** (A record of type: EcoATM_PWSIntegration.DesposcoAPIs)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Create Variable**
3. **Rest Call** ⚠️ *(This step has a safety catch if it fails)*
4. **Run another process: "EcoATM_PWSIntegration.ACT_AuditRestAPICalls"**
5. **Import Xml**
6. **Aggregate List
      - Store the result in a new variable called **$Count****
7. **Change List**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
