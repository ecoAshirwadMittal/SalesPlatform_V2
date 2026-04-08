# Microflow Detailed Specification: Sub_TaskQueue_CreateAll

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **TaskQueueScheduler.TaskQueue**  (Result: **$TaskQueueList**)**
2. 🔀 **DECISION:** `$TaskQueueList=empty`
   ➔ **If [true]:**
      1. **Create **TaskQueueScheduler.TaskQueue** (Result: **$ScheduleQueue_1**)
      - Set **Name** = `'ScheduleQueue_1'`
      - Set **FullName** = `@TaskQueueScheduler.ModuleName+'.ScheduleQueue_1'`
      - Set **ShortName** = `'1'`
      - Set **Description** = `'First queue for scheduler'`
      - Set **AllowScheduling** = `true`**
      2. **Create **TaskQueueScheduler.TaskQueue** (Result: **$ScheduleQueue_2**)
      - Set **Name** = `'ScheduleQueue_2'`
      - Set **FullName** = `@TaskQueueScheduler.ModuleName+'.ScheduleQueue_2'`
      - Set **ShortName** = `'2'`
      - Set **Description** = `'Second queue for scheduler'`
      - Set **AllowScheduling** = `true`**
      3. **Create **TaskQueueScheduler.TaskQueue** (Result: **$ScheduleQueue_3**)
      - Set **Name** = `'ScheduleQueue_3'`
      - Set **FullName** = `@TaskQueueScheduler.ModuleName+'.ScheduleQueue_3'`
      - Set **ShortName** = `'3'`
      - Set **Description** = `'Third queue for scheduler'`
      - Set **AllowScheduling** = `true`**
      4. **Create **TaskQueueScheduler.TaskQueue** (Result: **$Batch**)
      - Set **Name** = `'Batch'`
      - Set **FullName** = `@TaskQueueScheduler.ModuleName+'.Batch'`
      - Set **ShortName** = `'Batch'`
      - Set **Description** = `'Queue for microflow execution in background'`
      - Set **AllowScheduling** = `false`**
      5. **Create **TaskQueueScheduler.TaskQueue** (Result: **$Batch3Threads**)
      - Set **Name** = `'Batch3Threads'`
      - Set **FullName** = `@TaskQueueScheduler.ModuleName+'.Batch3Threads'`
      - Set **ShortName** = `'Batch3'`
      - Set **Description** = `'Queue with 3 threads for parallel microflow execution in background'`
      - Set **AllowScheduling** = `false`**
      6. **Create **TaskQueueScheduler.TaskQueue** (Result: **$Batch1Clusterwide**)
      - Set **Name** = `'Batch1Clusterwide'`
      - Set **FullName** = `@TaskQueueScheduler.ModuleName+'.Batch1Clusterwide'`
      - Set **ShortName** = `'Batch1'`
      - Set **Description** = `'Queue with 1 thread clusterwide for sequential (1 by 1) microflow execution in background'`
      - Set **AllowScheduling** = `false`**
      7. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.