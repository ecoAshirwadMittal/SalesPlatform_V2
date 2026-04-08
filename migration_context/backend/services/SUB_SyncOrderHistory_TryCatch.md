# Microflow Detailed Specification: SUB_SyncOrderHistory_TryCatch

### 📥 Inputs (Parameters)
- **$DesposcoAPIsForOrderHistory** (Type: EcoATM_PWSIntegration.DesposcoAPIs)
- **$IteratorOrder** (Type: EcoATM_PWS.Order)
- **$OrderStatusList** (Type: EcoATM_PWS.OrderStatus)
- **$DesposcoAPIsList** (Type: EcoATM_PWSIntegration.DesposcoAPIs)
- **$EncodedAuth** (Type: Variable)
- **$DeposcoConfig** (Type: EcoATM_PWSIntegration.DeposcoConfig)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **EcoATM_PWSIntegration.Sub_SyncOrderHistory****
2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.