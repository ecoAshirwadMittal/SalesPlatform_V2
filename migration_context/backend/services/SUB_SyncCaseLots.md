# Microflow Detailed Specification: SUB_SyncCaseLots

### 📥 Inputs (Parameters)
- **$EncodedAuth** (Type: Variable)
- **$PageNo** (Type: Variable)
- **$StockUnitItemListAggregated** (Type: EcoATM_PWSIntegration.StockUnitItems)
- **$DeposcoConfig** (Type: EcoATM_PWSIntegration.DeposcoConfig)
- **$DesposcoAPI** (Type: EcoATM_PWSIntegration.DesposcoAPIs)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **Create Variable **$APIUrl** = `$DeposcoConfig/BaseURL + $DesposcoAPI/ServiceUrl+'&pageSize=1000&pageNo='+$PageNo`**
3. **RestCall**
4. **Call Microflow **EcoATM_PWSIntegration.ACT_AuditRestAPICalls****
5. **ImportXml**
6. **AggregateList**
7. **Add **$$StockUnitItemsList
** to/from list **$StockUnitItemListAggregated****
8. 🏁 **END:** Return `$Count>0`

**Final Result:** This process concludes by returning a [Boolean] value.