# Microflow Detailed Specification: Sub_MapProcessQueueToSchedule

### 📥 Inputs (Parameters)
- **$Process** (Type: ProcessQueue.Process)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **Process_QueueConfiguration** via Association from **$Process** (Result: **$SharedQueueConfiguration**)**
2. **Create Variable **$QueueName** = `if $SharedQueueConfiguration/QueueName='BatchProcesses' then 'Batch3Threads' else if $SharedQueueConfiguration/QueueName='Imports' then 'Imports' else 'Batch'`**
3. **Update Variable **$QueueName** = `@MicroflowScheduler.ModuleName +'.' +$QueueName`**
4. **DB Retrieve **MicroflowScheduler.Schedule** Filter: `[MicroflowName=$Process/MicroflowFullname or OldMicroflowName=$Process/MicroflowFullname] [QueueName=$QueueName]` (Result: **$Schedule**)**
5. 🔀 **DECISION:** `$Schedule!=empty`
   ➔ **If [false]:**
      1. **Create **MicroflowScheduler.Schedule** (Result: **$NewSchedule**)
      - Set **QueueName** = `$QueueName`
      - Set **MicroflowName** = `$Process/MicroflowFullname`
      - Set **Description** = `$Process/Name`**
      2. **Call Microflow **MicroflowScheduler.Sub_UPDSchedule****
      3. 🔀 **DECISION:** `$NewSchedule/MicroflowScheduler.Schedule_Microflows!=empty and $NewSchedule/MicroflowScheduler.Schedule_TaskQueue!=empty`
         ➔ **If [true]:**
            1. 🏁 **END:** Return `$NewSchedule`
         ➔ **If [false]:**
            1. 🏁 **END:** Return `empty`
   ➔ **If [true]:**
      1. 🏁 **END:** Return `empty`

**Final Result:** This process concludes by returning a [Object] value.