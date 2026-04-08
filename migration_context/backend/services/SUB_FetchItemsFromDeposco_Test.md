# Microflow Detailed Specification: SUB_FetchItemsFromDeposco_Test

### 📥 Inputs (Parameters)
- **$PageNo** (Type: Variable)
- **$DeposcoConfig** (Type: EcoATM_PWSIntegration.DeposcoConfig)
- **$DesposcoAPI** (Type: EcoATM_PWSIntegration.DesposcoAPIs)
- **$ItemInventoryItem_TestList** (Type: EcoATM_PWSIntegration.ItemInventoryItem_Test)
- **$FacilityInventoryItem_TestList** (Type: EcoATM_PWSIntegration.FacilityInventoryItem_Test)
- **$Inventory_TestList** (Type: EcoATM_PWSIntegration.Inventory_Test)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$LastSyncTime** = `if($DeposcoConfig/LastSyncTime!=empty) then addMinutes($DeposcoConfig/LastSyncTime,-1) else empty`**
2. **Call Microflow **EcoATM_PWSIntegration.SUB_GenerateDeposcoPassword** (Result: **$EncodedAuth**)**
3. **Create Variable **$APIUrl** = `$DeposcoConfig/BaseURL + $DesposcoAPI/ServiceUrl+'?pageNo='+$PageNo`**
4. **RestCall**
5. **Call Microflow **EcoATM_PWSIntegration.ACT_AuditRestAPICalls****
6. **ImportXml**
7. **Add **$$ItemList
** to/from list **$ItemInventoryItem_TestList****
8. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.