# Microflow Detailed Specification: SUB_ScheduleMicroflow

### 📥 Inputs (Parameters)
- **$TaskQueueName** (Type: Variable)
- **$CompleteName** (Type: Variable)
- **$NextRunTime** (Type: Variable)
- **$IntervalType** (Type: Variable)
- **$Interval** (Type: Variable)
- **$Description** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **TaskQueueScheduler.DS_MicroflowByName** (Result: **$Microflow**)**
2. 🔀 **DECISION:** `$Microflow != empty`
   ➔ **If [true]:**
      1. **DB Retrieve **TaskQueueScheduler.Schedule** Filter: `[MicroflowName = $Microflow/CompleteName]` (Result: **$ExistingSchedule**)**
      2. 🔀 **DECISION:** `$ExistingSchedule = empty`
         ➔ **If [true]:**
            1. **DB Retrieve **TaskQueueScheduler.TaskQueue** Filter: `[FullName=$TaskQueueName or Name=$TaskQueueName or ShortName=$TaskQueueName ] [AllowScheduling=true()]` (Result: **$TaskQueue**)**
            2. 🔀 **DECISION:** `$TaskQueue != empty`
               ➔ **If [true]:**
                  1. **Create **TaskQueueScheduler.Schedule** (Result: **$Schedule**)
      - Set **Schedule_TaskQueue** = `$TaskQueue`
      - Set **MicroflowName** = `$Microflow/CompleteName`
      - Set **Active** = `true`
      - Set **NextRunTime** = `$NextRunTime`
      - Set **ActiveFrom** = `parseDateTimeUTC('2000-01-01','yyyy-MM-dd')`
      - Set **IntervalType** = `$IntervalType`
      - Set **Interval** = `$Interval`
      - Set **Description** = `$Description`**
                  2. **Call Microflow **TaskQueueScheduler.Val_ProcessSchedule** (Result: **$Validations**)**
                  3. 🔀 **DECISION:** `$Validations=''`
                     ➔ **If [true]:**
                        1. **Commit/Save **$Schedule** to Database**
                        2. **LogMessage**
                        3. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. **JavaCallAction**
                        2. 🏁 **END:** Return empty
               ➔ **If [false]:**
                  1. **JavaCallAction**
                  2. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **LogMessage**
            2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **JavaCallAction**
      2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.