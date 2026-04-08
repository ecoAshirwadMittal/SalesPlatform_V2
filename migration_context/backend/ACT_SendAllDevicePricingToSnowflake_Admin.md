# Microflow Detailed Specification: ACT_SendAllDevicePricingToSnowflake_Admin

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'SendAllDevicePricingDataToSnowflakeAdmin'`**
2. **Create Variable **$Description** = `'Send All Price History to Snowflake'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer** (Result: **$Log**)**
4. **Call Microflow **EcoATM_PWS.DS_GetOrCreateMDMFuturePriceHelper** (Result: **$MDMFuturePriceHelper**)**
5. 🔀 **DECISION:** `$MDMFuturePriceHelper/FuturePWSPriceDate != empty`
   ➔ **If [false]:**
      1. **Show Message (Information): `Unable to send to snowflake: Future Price does not exist`**
      2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
      3. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **DB Retrieve **EcoATM_PWSMDM.Device** Filter: `[IsActive]` (Result: **$DeviceList_Active**)**
      2. 🔀 **DECISION:** `$DeviceList_Active != empty`
         ➔ **If [false]:**
            1. **Show Message (Information): `Unable to send to snowflake: No active devices exist`**
            2. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
            3. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **Call Microflow **EcoATM_PWS.ACT_SendPricingDataToSnowflake****
            2. **Call Microflow **Custom_Logging.SUB_Log_StartTimer** (Result: **$Log**)**
            3. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.