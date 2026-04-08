# Microflow Detailed Specification: ACT_CleanProcessedQueueTasks

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$CompletionDateLimit** = `addHours( [%CurrentDateTime%], -@TaskQueueScheduler.TasksCompletedOlderThanHours )`**
2. **Create Variable **$NonCompletionDateLimit** = `addHours( [%CurrentDateTime%], -@TaskQueueScheduler.TasksNonCompletedOlderThanHours )`**
3. **DB Retrieve **System.ProcessedQueueTask** Filter: `[ (Status = 'Completed' and Started < $CompletionDateLimit) or (Status != 'Completed' and Started < $NonCompletionDateLimit) ]` (Result: **$ProcessedQueueTaskList**)**
4. **AggregateList**
5. **JavaCallAction**
6. **Call Microflow **TaskQueueScheduler.Sub_QueuedAction_CreateBatches****
7. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.