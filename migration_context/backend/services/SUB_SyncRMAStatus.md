# Microflow Detailed Specification: SUB_SyncRMAStatus

### 📥 Inputs (Parameters)
- **$RMA** (Type: EcoATM_RMA.RMA)
- **$DesposcoAPI** (Type: EcoATM_PWSIntegration.DesposcoAPIs)
- **$DeposcoConfig** (Type: EcoATM_PWSIntegration.DeposcoConfig)
- **$RMAStatusList** (Type: EcoATM_RMA.RMAStatus)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$RetryNeeded** = `true`**
2. **Create Variable **$APIURLForRMA** = `$DesposcoAPI/ServiceUrl +'?number='+$RMA/OracleNumber`**
3. **Create Variable **$AccessTokenVar** = `empty`**
4. **Call Microflow **EcoATM_PWSIntegration.ACT_GenerateDeposcoV2Token** (Result: **$AccessToken_2**)**
5. **Update Variable **$AccessTokenVar** = `$AccessToken_2`**
6. **RestCall**
7. **Call Microflow **EcoATM_PWSIntegration.ACT_AuditRestAPICalls****
8. 🔀 **DECISION:** `valid response?`
   ➔ **If [true]:**
      1. **ImportXml**
      2. **Create Variable **$sendToSnowflake** = `$RMAResponse/OrderStatus!=empty and $RMAResponse/OrderStatus!= $RMA/EcoATM_RMA.RMA_RMAStatus/EcoATM_RMA.RMAStatus/SystemStatus`**
      3. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/SystemStatus=$RMAResponse/OrderStatus` (Result: **$RMAStatus**)**
      4. **Update **$RMA**
      - Set **RMA_RMAStatus** = `if $RMAStatus!=empty then $RMAStatus else $RMA/EcoATM_RMA.RMA_RMAStatus`**
      5. 🔀 **DECISION:** `$sendToSnowflake`
         ➔ **If [true]:**
            1. **Call Microflow **EcoATM_RMA.SUB_SendOnlyRMADetailsToSnowflake****
            2. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🔀 **DECISION:** `$RetryNeeded`
         ➔ **If [false]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_Error****
            2. 🏁 **END:** Return empty
         ➔ **If [true]:**
            1. **Update Variable **$RetryNeeded** = `false`**
            2. **JavaCallAction**
               *(Merging with existing path logic)*

**Final Result:** This process concludes by returning a [Void] value.