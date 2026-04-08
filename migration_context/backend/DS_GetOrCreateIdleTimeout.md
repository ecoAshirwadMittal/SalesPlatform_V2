# Microflow Detailed Specification: DS_GetOrCreateIdleTimeout

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_Direct_Theme.IdleTimeout** Filter: `[EcoATM_Direct_Theme.IdleTimeout_Session = $currentSession]` (Result: **$IdleTimeout**)**
2. 🔀 **DECISION:** `$IdleTimeout != empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$IdleTimeout`
   ➔ **If [false]:**
      1. **Create **EcoATM_Direct_Theme.IdleTimeout** (Result: **$NewIdleTimeout**)
      - Set **LastRecordedActivity** = `[%CurrentDateTime%]`
      - Set **IdleTimeout_Session** = `$currentSession`**
      2. 🏁 **END:** Return `$NewIdleTimeout`

**Final Result:** This process concludes by returning a [Object] value.