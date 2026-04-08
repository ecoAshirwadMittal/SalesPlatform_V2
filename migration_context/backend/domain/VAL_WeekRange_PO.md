# Microflow Detailed Specification: VAL_WeekRange_PO

### 📥 Inputs (Parameters)
- **$PurchaseOrder** (Type: EcoATM_PO.PurchaseOrder)
- **$ToWeek** (Type: EcoATM_MDM.Week)
- **$FromWeek** (Type: EcoATM_MDM.Week)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$ValidationPassed** = `true`**
2. 🔀 **DECISION:** `$FromWeek != empty`
   ➔ **If [true]:**
      1. 🔀 **DECISION:** `$ToWeek != empty and $ToWeek/WeekNumber>= $FromWeek/WeekNumber`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$ValidationPassed`
               ➔ **If [true]:**
                  1. **DB Retrieve **EcoATM_PO.WeekPeriod** Filter: `[$FromWeek/WeekStartDateTime<=EcoATM_PO.WeekPeriod_Week/EcoATM_MDM.Week/WeekStartDateTime and $ToWeek/WeekStartDateTime>=EcoATM_PO.WeekPeriod_Week/EcoATM_MDM.Week/WeekStartDateTime]` (Result: **$WeekPeriodList**)**
                  2. 🔀 **DECISION:** `$WeekPeriodList=empty`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return `false`
                     ➔ **If [true]:**
                        1. 🏁 **END:** Return `true`
               ➔ **If [false]:**
                  1. 🏁 **END:** Return `false`
         ➔ **If [false]:**
            1. **Update Variable **$ValidationPassed** = `false`**
            2. **ValidationFeedback**
            3. 🔀 **DECISION:** `$ValidationPassed`
               ➔ **If [true]:**
                  1. **DB Retrieve **EcoATM_PO.WeekPeriod** Filter: `[$FromWeek/WeekStartDateTime<=EcoATM_PO.WeekPeriod_Week/EcoATM_MDM.Week/WeekStartDateTime and $ToWeek/WeekStartDateTime>=EcoATM_PO.WeekPeriod_Week/EcoATM_MDM.Week/WeekStartDateTime]` (Result: **$WeekPeriodList**)**
                  2. 🔀 **DECISION:** `$WeekPeriodList=empty`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return `false`
                     ➔ **If [true]:**
                        1. 🏁 **END:** Return `true`
               ➔ **If [false]:**
                  1. 🏁 **END:** Return `false`
   ➔ **If [false]:**
      1. **Update Variable **$ValidationPassed** = `false`**
      2. **ValidationFeedback**
      3. 🔀 **DECISION:** `$ToWeek != empty and $ToWeek/WeekNumber>= $FromWeek/WeekNumber`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$ValidationPassed`
               ➔ **If [true]:**
                  1. **DB Retrieve **EcoATM_PO.WeekPeriod** Filter: `[$FromWeek/WeekStartDateTime<=EcoATM_PO.WeekPeriod_Week/EcoATM_MDM.Week/WeekStartDateTime and $ToWeek/WeekStartDateTime>=EcoATM_PO.WeekPeriod_Week/EcoATM_MDM.Week/WeekStartDateTime]` (Result: **$WeekPeriodList**)**
                  2. 🔀 **DECISION:** `$WeekPeriodList=empty`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return `false`
                     ➔ **If [true]:**
                        1. 🏁 **END:** Return `true`
               ➔ **If [false]:**
                  1. 🏁 **END:** Return `false`
         ➔ **If [false]:**
            1. **Update Variable **$ValidationPassed** = `false`**
            2. **ValidationFeedback**
            3. 🔀 **DECISION:** `$ValidationPassed`
               ➔ **If [true]:**
                  1. **DB Retrieve **EcoATM_PO.WeekPeriod** Filter: `[$FromWeek/WeekStartDateTime<=EcoATM_PO.WeekPeriod_Week/EcoATM_MDM.Week/WeekStartDateTime and $ToWeek/WeekStartDateTime>=EcoATM_PO.WeekPeriod_Week/EcoATM_MDM.Week/WeekStartDateTime]` (Result: **$WeekPeriodList**)**
                  2. 🔀 **DECISION:** `$WeekPeriodList=empty`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return `false`
                     ➔ **If [true]:**
                        1. 🏁 **END:** Return `true`
               ➔ **If [false]:**
                  1. 🏁 **END:** Return `false`

**Final Result:** This process concludes by returning a [Boolean] value.