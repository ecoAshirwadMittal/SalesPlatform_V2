# Microflow Detailed Specification: Sub_PausedSchedule_Check

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **TaskQueueScheduler.DS_PausedSchedule_Get** (Result: **$PausedSchedule**)**
2. 🔀 **DECISION:** `$PausedSchedule!=empty`
   ➔ **If [true]:**
      1. **Call Microflow **TaskQueueScheduler.Sub_PreviousInstance_Check** (Result: **$StillRunning**)**
      2. 🔀 **DECISION:** `$StillRunning and $PausedSchedule/PausedUntil>[%CurrentDateTime%]`
         ➔ **If [true]:**
            1. 🏁 **END:** Return `$PausedSchedule`
         ➔ **If [false]:**
            1. **Call Microflow **TaskQueueScheduler.Sub_PausedSchedule_Reactivate****
            2. 🏁 **END:** Return `empty`
   ➔ **If [false]:**
      1. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [Object] value.