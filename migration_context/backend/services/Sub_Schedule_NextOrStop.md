# Microflow Detailed Specification: Sub_Schedule_NextOrStop

### 📥 Inputs (Parameters)
- **$TaskQueueName** (Type: Variable)
- **$MicroflowName** (Type: Variable)
- **$NextRunTime** (Type: Variable)
- **$IntervalType** (Type: Variable)
- **$Interval** (Type: Variable)
- **$Description** (Type: Variable)
- **$MaxBatches** (Type: Variable)
- **$BatchCount** (Type: Variable)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$BatchCount!=empty and ($BatchCount>0 or $BatchCount=-1) and $MaxBatches!=empty and $MaxBatches>0`
   ➔ **If [true]:**
      1. **DB Retrieve **TaskQueueScheduler.Schedule** Filter: `[MicroflowName=$MicroflowName]` (Result: **$Schedule**)**
      2. **DB Retrieve **TaskQueueScheduler.TaskQueue** Filter: `[FullName=$TaskQueueName or Name=$TaskQueueName or ShortName=$TaskQueueName ] [AllowScheduling=true()]` (Result: **$TaskQueue**)**
      3. 🔀 **DECISION:** `$TaskQueue != empty`
         ➔ **If [true]:**
            1. 🔀 **DECISION:** `$Schedule!=empty`
               ➔ **If [false]:**
                  1. 🔀 **DECISION:** `$BatchCount=$MaxBatches`
                     ➔ **If [false]:**
                        1. 🏁 **END:** Return empty
                     ➔ **If [true]:**
                        1. **Create **TaskQueueScheduler.Schedule** (Result: **$NewSchedule**)
      - Set **QueueName** = `$TaskQueue/FullName`
      - Set **Schedule_TaskQueue** = `$TaskQueue`
      - Set **MicroflowName** = `$MicroflowName`
      - Set **Active** = `true`
      - Set **NextRunTime** = `if $NextRunTime=empty then addMinutes([%CurrentDateTime%],3) else $NextRunTime`
      - Set **ActiveFrom** = `[%CurrentDateTime%]`
      - Set **IntervalType** = `if $IntervalType=empty then TaskQueueScheduler.Enum_IntervalType.Minute else $IntervalType`
      - Set **Interval** = `if $Interval=empty or $Interval<=0 then 5 else $Interval`
      - Set **Description** = `if $Description=empty then 'Start next batches' else $Description`**
                        2. 🏁 **END:** Return empty
               ➔ **If [true]:**
                  1. 🔀 **DECISION:** `$BatchCount=$MaxBatches and ( $Schedule/Active=false or $Schedule/NextRunTime=empty or $Schedule/ActiveFrom=empty or ($Schedule/ActiveUntil!=empty and $Schedule/ActiveUntil<$NextRunTime) or $Schedule/QueueName!=$TaskQueue/FullName or $Schedule/Interval=empty or ($Schedule/Interval=empty or $Schedule/Interval<=0) )`
                     ➔ **If [true]:**
                        1. **Update **$Schedule** (and Save to DB)
      - Set **Active** = `true`
      - Set **NextRunTime** = `if $Schedule/NextRunTime=empty then if $NextRunTime=empty then addMinutes([%CurrentDateTime%],3) else $NextRunTime else $Schedule/NextRunTime`
      - Set **ActiveFrom** = `if $Schedule/ActiveFrom=empty then [%CurrentDateTime%] else $Schedule/ActiveFrom`
      - Set **ActiveUntil** = `if ($Schedule/ActiveUntil!=empty and $Schedule/ActiveUntil<$NextRunTime) then empty else $Schedule/ActiveUntil`
      - Set **QueueName** = `$TaskQueue/FullName`
      - Set **Schedule_TaskQueue** = `$TaskQueue`
      - Set **IntervalType** = `if $IntervalType=empty then if $Schedule/IntervalType=empty then TaskQueueScheduler.Enum_IntervalType.Minute else $Schedule/IntervalType else $IntervalType`
      - Set **Interval** = `if $Interval=empty or $Interval<=0 then if $Schedule/Interval=empty or $Schedule/Interval<=0 then 5 else $Schedule/Interval else $Interval`**
                        2. 🏁 **END:** Return empty
                     ➔ **If [false]:**
                        1. 🔀 **DECISION:** `$BatchCount<$MaxBatches and ( $Schedule/Active=true or $Schedule/NextRunTime!=empty )`
                           ➔ **If [false]:**
                              1. 🏁 **END:** Return empty
                           ➔ **If [true]:**
                              1. **Update **$Schedule** (and Save to DB)
      - Set **Active** = `false`
      - Set **NextRunTime** = `empty`**
                              2. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.