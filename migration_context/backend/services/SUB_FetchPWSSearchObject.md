# Microflow Detailed Specification: SUB_FetchPWSSearchObject

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **PWSSearch_Session** via Association from **$currentSession** (Result: **$PWSSearchList**)**
2. 🔀 **DECISION:** `$PWSSearchList!=empty`
   ➔ **If [true]:**
      1. **List Operation: **Sort** on **$undefined** sorted by: changedDate (Descending) (Result: **$NewPWSSearchList**)**
      2. **List Operation: **Head** on **$undefined** (Result: **$LatestSearch**)**
      3. 🏁 **END:** Return `$LatestSearch`
   ➔ **If [false]:**
      1. **Create **EcoATM_PWS.PWSSearch** (Result: **$NewPWSSearch**)
      - Set **PWSSearch_Session** = `$currentSession`**
      2. 🏁 **END:** Return `$NewPWSSearch`

**Final Result:** This process concludes by returning a [Object] value.