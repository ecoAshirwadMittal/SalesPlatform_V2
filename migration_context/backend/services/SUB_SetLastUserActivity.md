# Microflow Detailed Specification: SUB_SetLastUserActivity

### 📥 Inputs (Parameters)
- **$IdleTimeout** (Type: EcoATM_Direct_Theme.IdleTimeout)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$IdleTimeout = empty`
   ➔ **If [true]:**
      1. **Retrieve related **IdleTimeout_Session** via Association from **$currentSession** (Result: **$IdleTimeoutList**)**
      2. **List Operation: **Sort** on **$undefined** sorted by: changedDate (Descending) (Result: **$IdleTimeoutList_Sorted**)**
      3. **List Operation: **Head** on **$undefined** (Result: **$IdleTimeout_Existing**)**
      4. **Update **$IdleTimeout_Existing** (and Save to DB)
      - Set **LastRecordedActivity** = `[%CurrentDateTime%]`**
      5. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Update **$IdleTimeout** (and Save to DB)
      - Set **LastRecordedActivity** = `[%CurrentDateTime%]`
      - Set **IdleTimeout_Session** = `$currentSession`**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.