# Microflow Detailed Specification: DS_LastUserActivity

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **IdleTimeout_Session** via Association from **$currentSession** (Result: **$IdleTimeoutList**)**
2. **List Operation: **Sort** on **$undefined** sorted by: changedDate (Descending) (Result: **$IdleTimeoutList_Sorted**)**
3. **List Operation: **Head** on **$undefined** (Result: **$IdleTimeout**)**
4. **Create **EcoATM_Direct_Theme.UiHelper** (Result: **$NewUiHelper**)
      - Set **DateTime** = `$IdleTimeout/LastRecordedActivity`**
5. 🏁 **END:** Return `$NewUiHelper`

**Final Result:** This process concludes by returning a [Object] value.