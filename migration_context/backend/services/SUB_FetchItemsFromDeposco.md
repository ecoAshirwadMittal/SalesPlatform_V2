# Microflow Detailed Specification: SUB_FetchItemsFromDeposco

### 📥 Inputs (Parameters)
- **$PageNo** (Type: Variable)
- **$ItemInventoryItemList** (Type: EcoATM_PWSIntegration.ItemInventoryItem)
- **$FullInventorySync** (Type: Variable)
- **$DeposcoConfig** (Type: EcoATM_PWSIntegration.DeposcoConfig)
- **$DesposcoAPI** (Type: EcoATM_PWSIntegration.DesposcoAPIs)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$LastSyncTime** = `if($DeposcoConfig/LastSyncTime!=empty) then addMinutes($DeposcoConfig/LastSyncTime,-1) else empty`**
2. **Call Microflow **EcoATM_PWSIntegration.SUB_GenerateDeposcoPassword** (Result: **$EncodedAuth**)**
3. **Create Variable **$APIUrl** = `if($FullInventorySync or $LastSyncTime=empty) then $DeposcoConfig/BaseURL + $DesposcoAPI/ServiceUrl+'?pageNo='+$PageNo else $DeposcoConfig/BaseURL + $DesposcoAPI/ServiceUrl+'?startActivityTime='+formatDateTimeUTC($LastSyncTime, 'yyyy-MM-dd''T''HH:mm:ss''Z''')+'&pageNo='+$PageNo`**
4. **RestCall**
5. **Call Microflow **EcoATM_PWSIntegration.ACT_AuditRestAPICalls****
6. **ImportXml**
7. **AggregateList**
8. **Call Microflow **Custom_Logging.SUB_Log_Info****
9. **Add **$$ItemInventory
** to/from list **$ItemInventoryItemList****
10. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.