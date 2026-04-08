# Microflow Analysis: SUB_SyncCaseLots_TryCatch

### Requirements (Inputs):
- **$EncodedAuth** (A record of type: Object)
- **$PageNo** (A record of type: Object)
- **$StockUnitItemListAggregated** (A record of type: EcoATM_PWSIntegration.StockUnitItems)
- **$DeposcoConfig** (A record of type: EcoATM_PWSIntegration.DeposcoConfig)
- **$DesposcoAPI** (A record of type: EcoATM_PWSIntegration.DesposcoAPIs)

### Execution Steps:
1. **Run another process: "EcoATM_PWS.SUB_SyncCaseLots"
      - Store the result in a new variable called **$Response****
2. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
