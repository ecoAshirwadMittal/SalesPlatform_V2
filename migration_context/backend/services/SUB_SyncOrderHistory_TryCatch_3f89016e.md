# Microflow Analysis: SUB_SyncOrderHistory_TryCatch

### Requirements (Inputs):
- **$DesposcoAPIsForOrderHistory** (A record of type: EcoATM_PWSIntegration.DesposcoAPIs)
- **$IteratorOrder** (A record of type: EcoATM_PWS.Order)
- **$OrderStatusList** (A record of type: EcoATM_PWS.OrderStatus)
- **$DesposcoAPIsList** (A record of type: EcoATM_PWSIntegration.DesposcoAPIs)
- **$EncodedAuth** (A record of type: Object)
- **$DeposcoConfig** (A record of type: EcoATM_PWSIntegration.DeposcoConfig)

### Execution Steps:
1. **Run another process: "EcoATM_PWSIntegration.Sub_SyncOrderHistory"** ⚠️ *(This step has a safety catch if it fails)*
2. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
