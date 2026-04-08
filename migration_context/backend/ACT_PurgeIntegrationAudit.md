# Microflow Detailed Specification: ACT_PurgeIntegrationAudit

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$IntegrationAuditPurge** = `'IntegrationAuditPurge'`**
2. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
3. **Create Variable **$DataExists** = `true`**
4. **Create Variable **$PurgeDate** = `addDays([%BeginOfCurrentDay%],-1* @EcoATM_PWSIntegration.IntegrationAuditRetentionPeriod)`**
5. **Create Variable **$Offset** = `5000`**
6. 🔄 **LOOP:** For each **$undefined** in **$undefined**
   │ 1. **DB Retrieve **EcoATM_PWSIntegration.Integration** Filter: `[createdDate<=$PurgeDate]` (Result: **$IntegrationList**)**
   │ 2. 🔀 **DECISION:** `$IntegrationList!=empty`
   │    ➔ **If [true]:**
   │       1. **Delete**
   │    ➔ **If [false]:**
   │       1. **Update Variable **$DataExists** = `false`**
   └─ **End Loop**
7. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
8. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.