# Microflow Detailed Specification: SUB_LoadPWSInventory_Deposco

### 📥 Inputs (Parameters)
- **$FullInventorySync** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Eco_Core.ACT_FeatureFlag_RetrieveOrCreate** (Result: **$FeatureFlagState**)**
2. 🔀 **DECISION:** `$FeatureFlagState = true`
   ➔ **If [true]:**
      1. **Create Variable **$TimerName** = `'PWSInventorySync'`**
      2. **Create Variable **$Description** = `if($FullInventorySync) then 'PWS Full Inventory Sync ' else 'PWS Delta Inventory Sync.'`**
      3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
      4. **DB Retrieve **EcoATM_PWSIntegration.PWSInventory**  (Result: **$PWSInventory**)**
      5. **DB Retrieve **EcoATM_PWSIntegration.DeposcoConfig**  (Result: **$DeposcoConfig**)**
      6. **Create Variable **$PWSDeviceCount** = `$PWSInventory/DeviceCount`**
      7. **Create Variable **$PageCount** = `if($DeposcoConfig/PageCount!=empty) then max($DeposcoConfig/PageCount,ceil($PWSInventory/DeviceCount div 1000))+1 else ceil($PWSDeviceCount div 1000) +1`**
      8. **Retrieve related **DesposcoAPIs_DeposcoConfig** via Association from **$DeposcoConfig** (Result: **$DesposcoAPIsList**)**
      9. **List Operation: **Find** on **$undefined** where `EcoATM_PWSIntegration.ENUM_DeposcoServices.Inventory` (Result: **$DesposcoAPI**)**
      10. **CreateList**
      11. **Create Variable **$SyncBeginTime** = `[%CurrentDateTime%]`**
      12. 🔄 **LOOP:** For each **$undefined** in **$undefined**
         │ 1. **Call Microflow **Custom_Logging.SUB_Log_Info****
         │ 2. **Call Microflow **EcoATM_PWS.SUB_LoadPWSInventory_Task_Deposco** (Result: **$DeviceCount**)**
         │ 3. **Update Variable **$PageCount** = `$PageCount-1`**
         └─ **End Loop**
      13. **Update **$DeposcoConfig** (and Save to DB)
      - Set **LastSyncTime** = `$SyncBeginTime`**
      14. **CreateList**
      15. **DB Retrieve **EcoATM_PWSMDM.Device**  (Result: **$DeviceList**)**
      16. **Create Variable **$MissingDevices** = `empty`**
      17. **Create Variable **$LogMessage** = `empty`**
      18. 🔄 **LOOP:** For each **$IteratorInventoryItem** in **$ItemInventoryItemList**
         │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/SKU=$IteratorInventoryItem/ItemNumber` (Result: **$Device**)**
         │ 2. 🔀 **DECISION:** `$Device != empty`
         │    ➔ **If [true]:**
         │       1. **Retrieve related **FacilityInventoryItem_ItemInventoryItem** via Association from **$IteratorInventoryItem** (Result: **$FacilityInventoryItemList**)**
         │       2. **Create Variable **$ATPForDevice** = `0`**
         │       3. 🔄 **LOOP:** For each **$IteratorFacilityInventoryItem** in **$FacilityInventoryItemList**
         │          │ 1. **Retrieve related **Inventory_FacilityInventoryItem** via Association from **$IteratorFacilityInventoryItem** (Result: **$Inventory**)**
         │          │ 2. **Update Variable **$ATPForDevice** = `if($Inventory/AvailableToPromise!=empty) then $ATPForDevice+ floor(parseDecimal($Inventory/AvailableToPromise)) else $ATPForDevice`**
         │          └─ **End Loop**
         │       4. 🔀 **DECISION:** `$FullInventorySync and $ATPForDevice!=$Device/AvailableQty`
         │          ➔ **If [false]:**
         │             1. **Create Variable **$Log** = `'Updating ATP for SKU:' + $IteratorInventoryItem/ItemNumber +' Current Avl Qty:' + $Device/AvailableQty +', New Avl Qty:'+$ATPForDevice`**
         │             2. **Update Variable **$LogMessage** = `if($LogMessage=empty) then $Log else $LogMessage +'; '+$Log`**
         │             3. **Update **$Device**
      - Set **ATPQty** = `$ATPForDevice`
      - Set **AvailableQty** = `$ATPForDevice`
      - Set **LastSyncTime** = `[%CurrentDateTime%]`
      - Set **DeposcoPageNo** = `$IteratorInventoryItem/PageNo`**
         │             4. **Add **$$Device
** to/from list **$DeviceWithDeltas****
         │          ➔ **If [true]:**
         │             1. **Call Microflow **Custom_Logging.SUB_Log_Warning****
         │             2. **Create Variable **$Log** = `'Updating ATP for SKU:' + $IteratorInventoryItem/ItemNumber +' Current Avl Qty:' + $Device/AvailableQty +', New Avl Qty:'+$ATPForDevice`**
         │             3. **Update Variable **$LogMessage** = `if($LogMessage=empty) then $Log else $LogMessage +'; '+$Log`**
         │             4. **Update **$Device**
      - Set **ATPQty** = `$ATPForDevice`
      - Set **AvailableQty** = `$ATPForDevice`
      - Set **LastSyncTime** = `[%CurrentDateTime%]`
      - Set **DeposcoPageNo** = `$IteratorInventoryItem/PageNo`**
         │             5. **Add **$$Device
** to/from list **$DeviceWithDeltas****
         │    ➔ **If [false]:**
         │       1. **Update Variable **$MissingDevices** = `if($MissingDevices=empty) then $IteratorInventoryItem/ItemNumber else $MissingDevices +', '+ $IteratorInventoryItem/ItemNumber`**
         └─ **End Loop**
      19. **AggregateList**
      20. **Call Microflow **Custom_Logging.SUB_Log_Info****
      21. **Call Microflow **EcoATM_PWS.SUB_UpdateReservedQuanityPerDevice****
      22. **Call Microflow **EcoATM_PWS.SUB_ProcessAvailableQuantityForFilters****
      23. **Call Microflow **EcoATM_PWS.SUB_CleanupMetaData****
      24. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      25. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Warning****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.