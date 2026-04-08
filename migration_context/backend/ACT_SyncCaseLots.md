# Microflow Detailed Specification: ACT_SyncCaseLots

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$JobStartTime** = `[%CurrentDateTime%]`**
2. **Create Variable **$Timer** = `'CaseLotInventorySync'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **DB Retrieve **EcoATM_PWSIntegration.DeposcoConfig**  (Result: **$DeposcoConfig**)**
5. **Retrieve related **DesposcoAPIs_DeposcoConfig** via Association from **$DeposcoConfig** (Result: **$DesposcoAPIsList**)**
6. **List Operation: **Find** on **$undefined** where `EcoATM_PWSIntegration.ENUM_DeposcoServices.StockUnit` (Result: **$DesposcoAPI**)**
7. **Create Variable **$NextPage** = `true`**
8. **Call Microflow **EcoATM_PWSIntegration.SUB_GenerateDeposcoPassword** (Result: **$EncodedAuth**)**
9. **JavaCallAction**
10. **CreateList**
11. **Create Variable **$PageNo** = `1`**
12. 🔄 **LOOP:** For each **$undefined** in **$undefined**
   │ 1. **Call Microflow **EcoATM_PWS.SUB_SyncCaseLots_TryCatch** (Result: **$Continue**)**
   │ 2. **Update Variable **$NextPage** = `$Continue`**
   │ 3. **Update Variable **$PageNo** = `$PageNo+1`**
   └─ **End Loop**
13. **Commit/Save **$StockUnitItemList** to Database**
14. **JavaCallAction**
15. **Create Variable **$OQL** = `'SELECT ItemNumber, CaseLotID,CaseLotSize , count(CaseLotID) AS CaseLotCount from ( select ItemNumber,ItemNumber+''-'' + sum(cast(Quantity as integer) ) as CaseLotID,sum(cast(Quantity as integer) ) as CaseLotSize from EcoATM_PWSIntegration.StockUnitItems where LpnNumber is not null and AllocatedOrderNumber is null group by ItemNumber,LpnNumber ) group by ItemNumber, CaseLotID,CaseLotSize order by ItemNumber, CaseLotID,CaseLotSize '`**
16. **JavaCallAction**
17. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[ItemType='SPB']` (Result: **$CaseLotDevices**)**
18. **CreateList**
19. 🔄 **LOOP:** For each **$IteratorGroupedCaseLots** in **$CaseLots**
   │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/SKU=$IteratorGroupedCaseLots/ItemNumber` (Result: **$DeviceExists**)**
   │ 2. 🔀 **DECISION:** `$DeviceExists!=empty`
   │    ➔ **If [true]:**
   │       1. **Retrieve related **CaseLot_Device** via Association from **$DeviceExists** (Result: **$CaseLotList**)**
   │       2. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/CaseLotID=$IteratorGroupedCaseLots/CaseLotID` (Result: **$CaseLotExists**)**
   │       3. 🔀 **DECISION:** `$CaseLotExists!=empty`
   │          ➔ **If [true]:**
   │             1. **DB Retrieve **System.User** Filter: `[id=$CaseLotExists/System.owner]` (Result: **$User**)**
   │             2. **Update **$CaseLotExists**
      - Set **CaseLotAvlQty** = `$IteratorGroupedCaseLots/CaseLotCount`
      - Set **CaseLotReservedQty** = `0`
      - Set **CaseLotATPQty** = `$IteratorGroupedCaseLots/CaseLotCount`
      - Set **CreatedBy** = `$User/Name`
      - Set **UpdatedBy** = `$currentUser/Name`
      - Set **IsActive** = `true`
      - Set **CaseLotPrice** = `$DeviceExists/CurrentListPrice * $CaseLotExists/CaseLotSize`**
   │             3. **Add **$$CaseLotExists
** to/from list **$CaseLotList_ToCommit****
   │          ➔ **If [false]:**
   │             1. **Create **EcoATM_PWSMDM.CaseLot** (Result: **$NewCaseLot**)
      - Set **CaseLotID** = `$IteratorGroupedCaseLots/CaseLotID`
      - Set **CaseLotSize** = `$IteratorGroupedCaseLots/CaseLotSize`
      - Set **CaseLotPrice** = `if($DeviceExists/CurrentListPrice!=empty) then $DeviceExists/CurrentListPrice * $IteratorGroupedCaseLots/CaseLotSize else 0`
      - Set **CaseLotAvlQty** = `$IteratorGroupedCaseLots/CaseLotCount`
      - Set **CaseLot_Device** = `$DeviceExists`
      - Set **CaseLotATPQty** = `$IteratorGroupedCaseLots/CaseLotCount`
      - Set **CreatedBy** = `$currentUser/Name`
      - Set **UpdatedBy** = `$currentUser/Name`**
   │             2. **Add **$$NewCaseLot
** to/from list **$CaseLotList_ToCommit****
   │    ➔ **If [false]:**
   │       1. **Call Microflow **Custom_Logging.SUB_Log_Debug****
   └─ **End Loop**
20. **Commit/Save **$CaseLotList_ToCommit** to Database**
21. **DB Retrieve **EcoATM_PWSMDM.CaseLot** Filter: `[changedDate<$JobStartTime] [IsActive]` (Result: **$CaseLotList_InActive**)**
22. 🔄 **LOOP:** For each **$IteratorCaseLot** in **$CaseLotList_InActive**
   │ 1. **Update **$IteratorCaseLot**
      - Set **CaseLotATPQty** = `0`
      - Set **CaseLotAvlQty** = `0`
      - Set **IsActive** = `false`**
   └─ **End Loop**
23. **Commit/Save **$CaseLotList_InActive** to Database**
24. **Call Microflow **EcoATM_PWS.SUB_UpdateReservedQuanityPerCaseLot****
25. **Call Microflow **EcoATM_PWS.SUB_SendCaseLotsToSnowflakeInitiate****
26. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
27. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.