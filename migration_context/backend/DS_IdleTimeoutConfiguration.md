# Microflow Detailed Specification: DS_IdleTimeoutConfiguration

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_Direct_Theme.IdleTimeoutConfiguration**  (Result: **$IdleTimeoutConfiguration**)**
2. 🔀 **DECISION:** `$IdleTimeoutConfiguration != empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `$IdleTimeoutConfiguration`
   ➔ **If [false]:**
      1. **Create **EcoATM_Direct_Theme.IdleTimeoutConfiguration** (Result: **$NewIdleTimeoutConfiguration**)
      - Set **IdleTimeoutWarning** = `300`
      - Set **MultiTabCheckInterval** = `10`
      - Set **WarningSeconds** = `59`
      - Set **TimerWarningMessage** = `'You have been idle for quite long now. You will be logged out soon.'`
      - Set **IsActive** = `false`**
      2. 🏁 **END:** Return `$NewIdleTimeoutConfiguration`

**Final Result:** This process concludes by returning a [Object] value.