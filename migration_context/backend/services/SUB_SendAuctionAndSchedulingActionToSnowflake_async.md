# Microflow Detailed Specification: SUB_SendAuctionAndSchedulingActionToSnowflake_async

### 📥 Inputs (Parameters)
- **$Week** (Type: EcoATM_MDM.Week)

### ⚙️ Execution Flow (Logic Steps)
1. **ExportXml**
2. **Create Variable **$Username** = `if $currentUser/Name=empty then 'System' else $currentUser/Name`**
3. **ExecuteDatabaseQuery**
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.