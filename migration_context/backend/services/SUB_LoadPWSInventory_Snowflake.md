# Microflow Detailed Specification: SUB_LoadPWSInventory_Snowflake

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Eco_Core.ACT_FeatureFlag_RetrieveOrCreate** (Result: **$FeatureFlagState**)**
2. 🔀 **DECISION:** `$FeatureFlagState = true`
   ➔ **If [true]:**
      1. **Create Variable **$TimerName** = `'PWSInventorySync'`**
      2. **Create Variable **$Description** = `'PWS Inventory Sync.'`**
      3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
      4. **Create Variable **$SQLStatement** = `'TRUNCATE TABLE ecoatm_pwsmdm$devicetemp'`**
      5. **JavaCallAction**
      6. **Create Variable **$StartTime** = `[%BeginOfCurrentDayUTC%]`**
      7. **Create Variable **$PWSDeviceCount** = `@EcoATM_PWSIntegration.CONST_SF_QueryPageSize`**
      8. **Create Variable **$Limit** = `$PWSDeviceCount`**
      9. **Create Variable **$Offset** = `0`**
      10. 🔄 **LOOP:** For each **$undefined** in **$undefined**
         │ 1. **Call Microflow **EcoATM_PWS.SUB_LoadPWSInventory_Task_Snowflake** (Result: **$DeviceCount**)**
         │ 2. **Update Variable **$Offset** = `$Offset + $Limit`**
         │ 3. **Update Variable **$PWSDeviceCount** = `if $DeviceCount != empty then $DeviceCount else $Limit`**
         └─ **End Loop**
      11. **Call Microflow **Custom_Logging.SUB_Log_Info****
      12. **JavaCallAction**
      13. **Call Microflow **Custom_Logging.SUB_Log_Info****
      14. **JavaCallAction**
      15. **DB Retrieve **EcoATM_PWSMDM.DeviceTemp** Filter: `[isNew]` (Result: **$DeviceTempList**)**
      16. 🔀 **DECISION:** `$DeviceTempList!=empty`
         ➔ **If [true]:**
            1. **CreateList**
            2. 🔄 **LOOP:** For each **$NewDeviceTemp** in **$DeviceTempList**
               │ 1. **Call Microflow **EcoATM_PWS.SUB_Device_CreateFromDeviceTemp** (Result: **$Device**)**
               │ 2. **Add **$$Device
** to/from list **$ToCommitDeviceList****
               └─ **End Loop**
            3. **AggregateList**
            4. **Call Microflow **Custom_Logging.SUB_Log_Info****
            5. **Commit/Save **$ToCommitDeviceList** to Database**
            6. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[LastUpdateDate < $StartTime]` (Result: **$DeviceList_Inactive**)**
            7. 🔀 **DECISION:** `$DeviceList_Inactive!=empty`
               ➔ **If [false]:**
                  1. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[IsActive]` (Result: **$DeviceList**)**
                  2. **Call Microflow **EcoATM_PWS.SUB_UpdateReservedQuanityPerDevice****
                  3. **Call Microflow **EcoATM_PWS.SUB_ProcessAvailableQuantityForFilters****
                  4. **Call Microflow **EcoATM_PWS.SUB_CleanupMetaData****
                  5. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                  6. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **Call Microflow **EcoATM_PWS.SUB_DisableInactiveDevices****
                  2. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[IsActive]` (Result: **$DeviceList**)**
                  3. **Call Microflow **EcoATM_PWS.SUB_UpdateReservedQuanityPerDevice****
                  4. **Call Microflow **EcoATM_PWS.SUB_ProcessAvailableQuantityForFilters****
                  5. **Call Microflow **EcoATM_PWS.SUB_CleanupMetaData****
                  6. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                  7. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[LastUpdateDate < $StartTime]` (Result: **$DeviceList_Inactive**)**
            2. 🔀 **DECISION:** `$DeviceList_Inactive!=empty`
               ➔ **If [false]:**
                  1. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[IsActive]` (Result: **$DeviceList**)**
                  2. **Call Microflow **EcoATM_PWS.SUB_UpdateReservedQuanityPerDevice****
                  3. **Call Microflow **EcoATM_PWS.SUB_ProcessAvailableQuantityForFilters****
                  4. **Call Microflow **EcoATM_PWS.SUB_CleanupMetaData****
                  5. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                  6. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. **Call Microflow **EcoATM_PWS.SUB_DisableInactiveDevices****
                  2. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[IsActive]` (Result: **$DeviceList**)**
                  3. **Call Microflow **EcoATM_PWS.SUB_UpdateReservedQuanityPerDevice****
                  4. **Call Microflow **EcoATM_PWS.SUB_ProcessAvailableQuantityForFilters****
                  5. **Call Microflow **EcoATM_PWS.SUB_CleanupMetaData****
                  6. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
                  7. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Warning****
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.