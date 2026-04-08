# Microflow Detailed Specification: Sub_PauseSchedule

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **TaskQueueScheduler.PausedSchedule**  (Result: **$PausedScheduleList**)**
2. **AggregateList**
3. 🔀 **DECISION:** `$Count=1`
   ➔ **If [true]:**
      1. **List Operation: **Head** on **$undefined** (Result: **$PausedSchedule**)**
      2. 🏁 **END:** Return `$PausedSchedule`
   ➔ **If [false]:**
      1. 🔀 **DECISION:** `$PausedScheduleList=empty`
         ➔ **If [true]:**
            1. **Create **TaskQueueScheduler.PausedSchedule** (Result: **$NewPausedSchedule**)
      - Set **PausedUntil** = `addMinutes([%CurrentDateTime%],@TaskQueueScheduler.PauseDurationMinutes)`**
            2. 🏁 **END:** Return `$NewPausedSchedule`
         ➔ **If [false]:**
            1. **Delete**
            2. **Create **TaskQueueScheduler.PausedSchedule** (Result: **$NewPausedSchedule**)
      - Set **PausedUntil** = `addMinutes([%CurrentDateTime%],@TaskQueueScheduler.PauseDurationMinutes)`**
            3. 🏁 **END:** Return `$NewPausedSchedule`

**Final Result:** This process concludes by returning a [Object] value.