# Microflow Detailed Specification: SUB_SyncCaseLots_TryCatch

### 📥 Inputs (Parameters)
- **$EncodedAuth** (Type: Variable)
- **$PageNo** (Type: Variable)
- **$StockUnitItemListAggregated** (Type: EcoATM_PWSIntegration.StockUnitItems)
- **$DeposcoConfig** (Type: EcoATM_PWSIntegration.DeposcoConfig)
- **$DesposcoAPI** (Type: EcoATM_PWSIntegration.DesposcoAPIs)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **EcoATM_PWS.SUB_SyncCaseLots** (Result: **$Response**)**
2. 🏁 **END:** Return `$Response`

**Final Result:** This process concludes by returning a [Boolean] value.