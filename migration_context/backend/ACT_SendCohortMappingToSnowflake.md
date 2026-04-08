# Microflow Detailed Specification: ACT_SendCohortMappingToSnowflake

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'SendCohortMappingToSnowflake'`**
2. **Create Variable **$Description** = `'Send Cohort Mapping to Snowflake'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer** (Result: **$Log**)**
4. **DB Retrieve **EcoATM_Reports.CohortMapping**  (Result: **$CohortMappingList**)**
5. **ExportXml**
6. **JavaCallAction**
7. **JavaCallAction**
8. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
9. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.