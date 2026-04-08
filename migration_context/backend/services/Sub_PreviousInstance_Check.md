# Microflow Detailed Specification: Sub_PreviousInstance_Check

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **TaskQueueScheduler.PreviousInstance**  (Result: **$PreviousInstanceList**)**
2. 🔀 **DECISION:** `$PreviousInstanceList!=empty`
   ➔ **If [true]:**
      1. **AggregateList**
      2. **DB Retrieve **System.XASInstance**  (Result: **$XASInstanceList**)**
      3. **AggregateList**
      4. **Create Variable **$CountSame** = `0`**
      5. **Create Variable **$StillRunning** = `false`**
      6. 🔄 **LOOP:** For each **$IteratorPreviousInstance** in **$PreviousInstanceList**
         │ 1. **List Operation: **Find** on **$undefined** where `$IteratorPreviousInstance/XASId` (Result: **$RunningInstance**)**
         │ 2. 🔀 **DECISION:** `$RunningInstance!=empty`
         │    ➔ **If [false]:**
         │    ➔ **If [true]:**
         │       1. **Update Variable **$CountSame** = `$CountSame+1`**
         │       2. **Update Variable **$StillRunning** = `true`**
         └─ **End Loop**
      7. 🔀 **DECISION:** `$CountPrevious=$CountCurrent and $CountCurrent=$CountSame and $CountPrevious=$CountSame`
         ➔ **If [false]:**
            1. 🏁 **END:** Return `$StillRunning`
         ➔ **If [true]:**
            1. 🏁 **END:** Return `false`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `true`

**Final Result:** This process concludes by returning a [Boolean] value.