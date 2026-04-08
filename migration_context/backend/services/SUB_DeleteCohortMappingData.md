# Microflow Detailed Specification: SUB_DeleteCohortMappingData

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'DeleteCohortMappingData'`**
2. **Create Variable **$Description** = `'Batch Delete Cohort Mapping Data'`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer** (Result: **$Log**)**
4. **DB Retrieve **EcoATM_Reports.CohortMapping**  (Result: **$CohortMappingList**)**
5. **AggregateList**
6. 🔀 **DECISION:** `$CohortMappingList != empty`
   ➔ **If [true]:**
      1. **Create Variable **$Amount** = `1000`**
      2. **Create Variable **$TotalProcessed** = `0`**
      3. **Create Variable **$Offset** = `0`**
      4. **JavaCallAction**
      5. **DB Retrieve **EcoATM_Reports.CohortMapping**  (Result: **$CohortMappingBatchList**)**
      6. **AggregateList**
      7. **Update Variable **$TotalProcessed** = `$TotalProcessed + $RetrievedCount`**
      8. **Delete**
      9. **JavaCallAction**
      10. **LogMessage**
      11. 🔀 **DECISION:** `$TotalProcessed >= $TotalItems`
         ➔ **If [true]:**
            1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer** (Result: **$Log**)**
            2. 🏁 **END:** Return empty
         ➔ **If [false]:**
               *(Merging with existing path logic)*
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_EndTimer** (Result: **$Log**)**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.