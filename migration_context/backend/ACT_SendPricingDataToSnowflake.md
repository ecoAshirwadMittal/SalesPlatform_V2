# Microflow Detailed Specification: ACT_SendPricingDataToSnowflake

### 📥 Inputs (Parameters)
- **$UpdatedDeviceList** (Type: EcoATM_PWSMDM.Device)
- **$FutureDate** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Eco_Core.ACT_FeatureFlag_RetrieveOrCreate** (Result: **$FeatureFlagState**)**
2. 🔀 **DECISION:** `$FeatureFlagState = true`
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Create Variable **$TimerName** = `'InitiateSendPricingDetailsToSnowflake'`**
      2. **Create Variable **$Description** = `'Initiate Sending Pricing Details to Snowflake'`**
      3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer** (Result: **$Log**)**
      4. **ExportXml**
      5. **Create Variable **$User** = `$currentUser/Name`**
      6. **Create Variable **$Date** = `if $FutureDate!=empty then formatDateTimeUTC($FutureDate,'yyyy-MM-dd') else empty`**
      7. **Call Microflow **EcoATM_PWS.SUB_SendDevicePricingDataToSnowflake****
      8. **Call Microflow **Custom_Logging.SUB_Log_EndTimer** (Result: **$Log**)**
      9. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.