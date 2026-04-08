# Microflow Detailed Specification: SUB_LoadPWSInvetory_UpdateConditionProperty

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **JavaCallAction**
3. 🔀 **DECISION:** `$NewConditionList!=empty`
   ➔ **If [true]:**
      1. **CreateList**
      2. 🔄 **LOOP:** For each **$IteratorCondition** in **$NewConditionList**
         │ 1. **Create **EcoATM_PWSMDM.Condition** (Result: **$NewCondition**)
      - Set **Condition** = `$IteratorCondition/Value`**
         │ 2. **Call Microflow **Custom_Logging.SUB_Log_Info****
         │ 3. **Add **$$NewCondition
** to/from list **$ConditionList****
         └─ **End Loop**
      3. **Commit/Save **$ConditionList** to Database**
      4. **Call Microflow **Custom_Logging.SUB_Log_Info****
      5. **JavaCallAction**
      6. **Call Microflow **Custom_Logging.SUB_Log_Info****
      7. **JavaCallAction**
      8. **Call Microflow **Custom_Logging.SUB_Log_Info****
      9. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Call Microflow **Custom_Logging.SUB_Log_Info****
      2. **JavaCallAction**
      3. **Call Microflow **Custom_Logging.SUB_Log_Info****
      4. **JavaCallAction**
      5. **Call Microflow **Custom_Logging.SUB_Log_Info****
      6. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.