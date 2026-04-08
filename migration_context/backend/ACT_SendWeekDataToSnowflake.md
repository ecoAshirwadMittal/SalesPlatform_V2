# Microflow Detailed Specification: ACT_SendWeekDataToSnowflake

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_MDM.Week**  (Result: **$WeekList**)**
2. **ExportXml**
3. **ExecuteDatabaseQuery**
4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.